/**
 * AirFlight Main Application Logic
 * Integrates interactive forms with real backend analytics via AirFlightAPI.
 * Design pattern: Input → Clear answer → 2–4 key metrics → One useful visualization → Optional details.
 */

const MONTH_NAMES = [
  "", "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

document.addEventListener('DOMContentLoaded', () => {
  initAirportsAndAirlines();
  initBestTimeScreen();
  initRouteReliabilityScreen();
  initRiskAdvisorScreen();
});

// Helper for status badge styling
function getRiskBadge(tier) {
  const t = (tier || '').toUpperCase();
  if (t === 'LOW' || t === 'EXCELLENT' || t === 'GOOD') {
    return {
      bg: 'bg-emerald-50 text-emerald-800 border-emerald-200',
      dot: 'bg-emerald-500',
      label: t === 'LOW' ? 'Low Historical Risk' : (t === 'EXCELLENT' ? 'High Reliability' : 'Good Reliability'),
      barColor: 'bg-emerald-600',
      textColor: 'text-emerald-700'
    };
  } else if (t === 'MEDIUM' || t === 'MODERATE') {
    return {
      bg: 'bg-amber-50 text-amber-800 border-amber-200',
      dot: 'bg-amber-500',
      label: (t === 'MEDIUM' || t === 'MODERATE') ? 'Moderate Historical Risk' : 'Moderate Reliability',
      barColor: 'bg-amber-500',
      textColor: 'text-amber-700'
    };
  } else {
    return {
      bg: 'bg-red-50 text-red-800 border-red-200',
      dot: 'bg-red-500',
      label: t === 'HIGH' ? 'High Historical Risk' : 'Low Reliability',
      barColor: 'bg-red-600',
      textColor: 'text-red-700'
    };
  }
}

// --------------------------------------------------------------------------
// 1. POPULATE AIRPORTS & AIRLINES
// --------------------------------------------------------------------------
async function initAirportsAndAirlines() {
  const airportData = await AirFlightAPI.getAirports();
  const airlineData = await AirFlightAPI.getAirlines();

  const airports = airportData.airports || [
    { code: "ORD", name: "ORD — Chicago O'Hare" },
    { code: "LGA", name: "LGA — New York LaGuardia" },
    { code: "JFK", name: "JFK — New York JFK" },
    { code: "EWR", name: "EWR — Newark Liberty" },
    { code: "LAX", name: "LAX — Los Angeles Intl" },
    { code: "SFO", name: "SFO — San Francisco Intl" },
    { code: "ATL", name: "ATL — Atlanta Hartsfield" },
    { code: "DFW", name: "DFW — Dallas/Fort Worth" },
    { code: "BOS", name: "BOS — Boston Logan" },
    { code: "SEA", name: "SEA — Seattle-Tacoma" }
  ];

  const airlines = airlineData.airlines || [
    { code: "UA", name: "United Airlines (UA)" },
    { code: "AA", name: "American Airlines (AA)" },
    { code: "DL", name: "Delta Air Lines (DL)" },
    { code: "WN", name: "Southwest Airlines (WN)" },
    { code: "B6", name: "JetBlue Airways (B6)" }
  ];

  // Populate airport selects
  document.querySelectorAll('select.airport-select').forEach(sel => {
    const currentVal = sel.getAttribute('data-default') || sel.value;
    sel.innerHTML = '';
    airports.forEach(ap => {
      const opt = document.createElement('option');
      opt.value = ap.code;
      opt.textContent = ap.name;
      if (ap.code === currentVal) opt.selected = true;
      sel.appendChild(opt);
    });
  });

  // Populate airline selects
  document.querySelectorAll('select.airline-select').forEach(sel => {
    const currentVal = sel.getAttribute('data-default') || sel.value;
    sel.innerHTML = '';
    airlines.forEach(al => {
      const opt = document.createElement('option');
      opt.value = al.code;
      opt.textContent = al.name;
      if (al.code === currentVal) opt.selected = true;
      sel.appendChild(opt);
    });
  });
}

// --------------------------------------------------------------------------
// 2. SCREEN 2: BEST TIME TO FLY
// --------------------------------------------------------------------------
function initBestTimeScreen() {
  const form = document.getElementById('best-time-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const origin = document.getElementById('bt-origin').value;
    const dest = document.getElementById('bt-dest').value;
    const monthVal = document.getElementById('bt-month').value;
    const month = (monthVal && monthVal !== 'all') ? parseInt(monthVal, 10) : null;

    const container = document.getElementById('best-time-results');
    if (!container) return;

    const displayMonthName = month ? MONTH_NAMES[month] : 'All months';

    // Loading State
    container.innerHTML = `
      <div class="py-12 flex flex-col items-center justify-center text-center space-y-3">
        <div class="w-9 h-9 border-3 border-primary border-t-transparent rounded-full animate-spin"></div>
        <p class="text-sm text-slate-600 font-medium">Evaluating historical windows for ${origin} → ${dest} (${displayMonthName})...</p>
      </div>
    `;
    container.classList.remove('hidden');

    try {
      const data = await AirFlightAPI.getBestTime(origin, dest, month);

      if (data.status === 'INSUFFICIENT_HISTORICAL_DATA') {
        container.innerHTML = `
          <div class="p-6 rounded-xl bg-white border border-slate-200 text-center space-y-2 shadow-sm">
            <span class="material-symbols-outlined text-slate-400 text-[32px]">info</span>
            <h3 class="text-base font-bold text-slate-900">Insufficient Historical Data</h3>
            <p class="text-sm text-slate-600 max-w-lg mx-auto">
              Only ${data.total_flights_found || 0} flights observed for <strong>${origin} → ${dest}</strong> ${month ? 'in ' + MONTH_NAMES[month] : ''} (minimum 10 required for reliability).
            </p>
          </div>
        `;
        return;
      }

      if (data.status !== 'SUCCESS') {
        throw new Error(data.message || 'Unable to retrieve best time analysis.');
      }

      const best = data.best_period_metrics || {};
      const allPeriods = data.all_periods_comparison || [];
      const evaluatedMonthLabel = data.month ? MONTH_NAMES[data.month] : 'All months';

      // Pattern: Input → Clear answer → 2–4 key metrics → One useful visualization → Optional details
      container.innerHTML = `
        <div class="space-y-6">
          <!-- 1. CLEAR ANSWER -->
          <div class="p-6 rounded-xl bg-white border border-blue-200 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div class="space-y-1.5">
              <div class="flex items-center gap-2">
                <span class="text-xs font-bold uppercase tracking-wider text-primary">Historically Best Window</span>
                <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold bg-blue-50 text-primary border border-blue-200">Recommended</span>
              </div>
              <h2 class="text-2xl sm:text-3xl font-extrabold text-slate-900">
                ${best.time_of_day || 'Morning'}
              </h2>
              <p class="text-sm text-slate-600 max-w-2xl">
                ${data.recommendation || `Historically, ${best.time_of_day} flights demonstrated the lowest delay rate (${best.delay_rate_pct}%) on this route.`}
              </p>
            </div>
            <div class="sm:text-right shrink-0 bg-slate-50 px-4 py-3 rounded-lg border border-slate-200 w-full sm:w-auto">
              <span class="text-xs text-slate-500 uppercase font-medium block">Evaluated Corridor</span>
              <span class="text-base font-bold text-slate-900">${data.origin} → ${data.destination}</span>
              <span class="text-xs text-slate-600 block mt-0.5">Month: <strong class="text-slate-800">${evaluatedMonthLabel}</strong> (${data.total_route_flights || 0} flights)</span>
            </div>
          </div>

          <!-- 2. 3 KEY METRICS -->
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Delay Rate (&gt;15 min)</span>
              <div class="mt-1 text-3xl font-extrabold text-primary">${best.delay_rate_pct || 0}%</div>
              <p class="mt-1.5 text-xs text-slate-500">During ${best.time_of_day || 'recommended'} window</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Avg Arrival Delay</span>
              <div class="mt-1 text-3xl font-extrabold text-slate-900">+${best.avg_arrival_delay_min || 0} <span class="text-sm font-normal text-slate-500">min</span></div>
              <p class="mt-1.5 text-xs text-slate-500">Across all flights in window</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Window Flights</span>
              <div class="mt-1 text-3xl font-extrabold text-slate-900">${best.flight_count || 0}</div>
              <p class="mt-1.5 text-xs text-slate-500">Of ${data.total_route_flights || 0} corridor flights</p>
            </div>
          </div>

          <!-- 3. ONE USEFUL VISUALIZATION: Delay Rate by Time of Day -->
          <div class="p-6 rounded-xl bg-white border border-slate-200 shadow-sm space-y-4">
            <div class="flex items-center justify-between">
              <div>
                <h3 class="text-base font-bold text-slate-900">Delay Rate by Departure Window</h3>
                <p class="text-xs text-slate-500">Comparing operational shifts for ${data.origin} → ${data.destination} (${evaluatedMonthLabel})</p>
              </div>
              <span class="text-xs text-slate-500 font-medium">Lower is better</span>
            </div>

            <div class="space-y-3 pt-2">
              ${allPeriods.map(p => {
                const isBest = p.time_of_day === data.historically_best_period;
                const barWidth = Math.min(100, Math.max(8, p.delay_rate_pct || 0));
                return `
                  <div class="p-3.5 rounded-lg ${isBest ? 'bg-blue-50/70 border border-blue-200' : 'bg-slate-50 border border-slate-100'} space-y-1.5">
                    <div class="flex items-center justify-between text-sm">
                      <div class="flex items-center gap-2">
                        <span class="font-semibold text-slate-900">${p.time_of_day}</span>
                        ${isBest ? '<span class="px-2 py-0.5 text-[11px] rounded-full bg-primary text-white font-semibold shadow-xs">Lowest Delay</span>' : ''}
                      </div>
                      <div class="text-slate-700 font-medium text-xs sm:text-sm">
                        <span class="${isBest ? 'text-primary font-bold' : ''}">${p.delay_rate_pct}% delayed</span>
                        <span class="text-slate-400 mx-1.5">•</span>
                        <span class="text-slate-500">+${p.avg_arrival_delay_min}m avg</span>
                      </div>
                    </div>
                    <div class="w-full h-2 bg-slate-200 rounded-full overflow-hidden">
                      <div class="h-full rounded-full transition-all duration-500 ${isBest ? 'bg-primary' : 'bg-slate-400'}" style="width: ${barWidth}%;"></div>
                    </div>
                  </div>
                `;
              }).join('')}
            </div>
          </div>

          <!-- 4. OPTIONAL DETAILS (Collapsible) -->
          <details class="p-4 rounded-xl bg-slate-50 border border-slate-200 group">
            <summary class="text-xs font-semibold text-slate-700 uppercase tracking-wider cursor-pointer flex items-center justify-between select-none">
              <span>View Shift Details &amp; Cancellation Rates</span>
              <span class="material-symbols-outlined text-[18px] text-slate-400 group-open:rotate-180 transition-transform">expand_more</span>
            </summary>
            <div class="mt-4 overflow-x-auto">
              <table class="w-full text-left text-xs sm:text-sm">
                <thead>
                  <tr class="border-b border-slate-200 text-xs text-slate-500 uppercase">
                    <th class="pb-2 font-semibold">Departure Window</th>
                    <th class="pb-2 font-semibold">Flights</th>
                    <th class="pb-2 font-semibold">Delay Rate</th>
                    <th class="pb-2 font-semibold">Avg Delay</th>
                    <th class="pb-2 font-semibold">Cancellation Rate</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                  ${allPeriods.map(p => `
                    <tr>
                      <td class="py-2.5 font-medium text-slate-900">${p.time_of_day}</td>
                      <td class="py-2.5 text-slate-600">${p.flight_count}</td>
                      <td class="py-2.5 font-semibold ${p.time_of_day === data.historically_best_period ? 'text-primary' : 'text-slate-900'}">${p.delay_rate_pct}%</td>
                      <td class="py-2.5 text-slate-600">+${p.avg_arrival_delay_min} min</td>
                      <td class="py-2.5 text-slate-600">${p.cancellation_rate_pct}%</td>
                    </tr>
                  `).join('')}
                </tbody>
              </table>
            </div>
          </details>
        </div>
      `;
    } catch (err) {
      container.innerHTML = `
        <div class="p-6 rounded-xl bg-red-50 border border-red-200 text-center space-y-3">
          <span class="material-symbols-outlined text-red-600 text-[32px]">error</span>
          <h3 class="text-base font-bold text-red-900">Analysis Error</h3>
          <p class="text-sm text-red-700">${err.message || 'Unable to connect to analytics engine.'}</p>
          <button onclick="document.getElementById('best-time-form').dispatchEvent(new Event('submit'))" class="px-4 py-2 bg-red-600 text-white rounded-lg font-medium text-sm hover:bg-red-700 transition-colors">
            Try Again
          </button>
        </div>
      `;
    }
  });
}

// --------------------------------------------------------------------------
// 3. SCREEN 3: ROUTE RELIABILITY
// --------------------------------------------------------------------------
function initRouteReliabilityScreen() {
  const form = document.getElementById('route-reliability-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const origin = document.getElementById('rr-origin').value;
    const dest = document.getElementById('rr-dest').value;

    const container = document.getElementById('route-reliability-results');
    if (!container) return;

    // Loading State
    container.innerHTML = `
      <div class="py-12 flex flex-col items-center justify-center text-center space-y-3">
        <div class="w-9 h-9 border-3 border-primary border-t-transparent rounded-full animate-spin"></div>
        <p class="text-sm text-slate-600 font-medium">Computing historical reliability score for ${origin} → ${dest}...</p>
      </div>
    `;
    container.classList.remove('hidden');

    try {
      const data = await AirFlightAPI.getRouteReliability(origin, dest);

      if (data.status === 'INSUFFICIENT_HISTORICAL_DATA') {
        container.innerHTML = `
          <div class="p-6 rounded-xl bg-white border border-slate-200 text-center space-y-2 shadow-sm">
            <span class="material-symbols-outlined text-slate-400 text-[32px]">info</span>
            <h3 class="text-base font-bold text-slate-900">Insufficient Historical Data</h3>
            <p class="text-sm text-slate-600 max-w-lg mx-auto">
              Only ${data.flights_found || 0} flights observed for <strong>${origin} → ${dest}</strong> (minimum 15 required for reliability scoring).
            </p>
          </div>
        `;
        return;
      }

      if (data.status !== 'SUCCESS') {
        throw new Error(data.message || 'Unable to retrieve route reliability.');
      }

      const badge = getRiskBadge(data.tier);
      const metrics = data.metrics || {};
      const best = data.best_period || {};
      const worst = data.most_challenging_period || {};

      // Pattern: Input → Clear answer → 2–4 key metrics → One useful visualization → Optional details
      container.innerHTML = `
        <div class="space-y-6">
          <!-- 1. CLEAR ANSWER (SCORE & LEVEL) -->
          <div class="p-6 rounded-xl bg-white border border-slate-200 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6">
            <div class="flex items-center gap-5">
              <!-- Score Circle Gauge -->
              <div class="relative w-24 h-24 shrink-0 flex items-center justify-center">
                <svg class="w-24 h-24 transform -rotate-90" viewBox="0 0 100 100">
                  <circle cx="50" cy="50" r="42" fill="none" stroke="#e2e8f0" stroke-width="8"></circle>
                  <circle cx="50" cy="50" r="42" fill="none" stroke="currentColor" class="text-primary transition-all duration-700"
                    stroke-dasharray="264" stroke-dashoffset="${264 - (264 * (data.reliability_score || 0) / 100)}"
                    stroke-linecap="round" stroke-width="8"></circle>
                </svg>
                <div class="absolute inset-0 flex flex-col items-center justify-center">
                  <span class="text-2xl font-black text-slate-900">${data.reliability_score}</span>
                  <span class="text-[9px] text-slate-400 font-bold uppercase">/ 100</span>
                </div>
              </div>
              <div class="space-y-1">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold border ${badge.bg}">
                  <span class="w-1.5 h-1.5 rounded-full ${badge.dot}"></span>
                  ${badge.label}
                </span>
                <h2 class="text-xl sm:text-2xl font-bold text-slate-900">${data.route || `${origin} → ${dest}`}</h2>
                <p class="text-xs sm:text-sm text-slate-500">
                  Empirical reliability score derived from 216,000 BTS flight records.
                </p>
              </div>
            </div>
            <div class="sm:text-right shrink-0 bg-slate-50 px-4 py-3 rounded-lg border border-slate-200 w-full sm:w-auto">
              <span class="text-xs text-slate-500 uppercase font-medium block">Route Sample Size</span>
              <span class="text-lg font-bold text-slate-900">${data.sample_size || 0} flights</span>
              <span class="text-xs text-slate-500 block">Civil aviation audit</span>
            </div>
          </div>

          <!-- 2. 4 KEY METRICS -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Delay Rate</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-slate-900">${metrics.delay_rate_pct || 0}%</div>
              <p class="mt-1.5 text-xs text-slate-500">${metrics.delayed_flights || 0} delayed flights</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Avg Arrival Delay</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-slate-900">+${metrics.avg_arrival_delay_min || 0} <span class="text-sm font-normal text-slate-500">min</span></div>
              <p class="mt-1.5 text-xs text-slate-500">Across all flights on corridor</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Cancellation Rate</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-slate-900">${metrics.cancellation_rate_pct || 0}%</div>
              <p class="mt-1.5 text-xs text-slate-500">${metrics.cancelled_flights || 0} cancellations</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Reliability Tier</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-primary">${data.tier || 'MODERATE'}</div>
              <p class="mt-1.5 text-xs text-slate-500">Deterministic benchmark</p>
            </div>
          </div>

          <!-- 3. COMPACT SUPPORTING VISUALIZATION: Best vs Most Challenging Period -->
          ${best.time_of_day ? `
            <div class="p-6 rounded-xl bg-white border border-slate-200 shadow-sm space-y-4">
              <h3 class="text-base font-bold text-slate-900">Shift Disruption Comparison</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="p-4 rounded-lg bg-emerald-50/60 border border-emerald-200 space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="px-2 py-0.5 rounded text-[11px] font-bold uppercase bg-emerald-100 text-emerald-800">Best Historical Window</span>
                    <span class="text-xs text-emerald-700 font-medium">${best.flight_count || 0} flights</span>
                  </div>
                  <h4 class="text-xl font-bold text-emerald-950">${best.time_of_day}</h4>
                  <div class="flex items-center gap-3 text-xs sm:text-sm text-emerald-900 font-medium">
                    <span><strong>${best.delay_rate_pct}%</strong> Delay Rate</span>
                    <span>•</span>
                    <span><strong>+${best.avg_arrival_delay_min}m</strong> Avg Delay</span>
                  </div>
                </div>

                <div class="p-4 rounded-lg bg-amber-50/60 border border-amber-200 space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="px-2 py-0.5 rounded text-[11px] font-bold uppercase bg-amber-100 text-amber-800">Most Challenging Window</span>
                    <span class="text-xs text-amber-700 font-medium">${worst.flight_count || 0} flights</span>
                  </div>
                  <h4 class="text-xl font-bold text-amber-950">${worst.time_of_day || 'Evening'}</h4>
                  <div class="flex items-center gap-3 text-xs sm:text-sm text-amber-900 font-medium">
                    <span><strong>${worst.delay_rate_pct || 0}%</strong> Delay Rate</span>
                    <span>•</span>
                    <span><strong>+${worst.avg_arrival_delay_min || 0}m</strong> Avg Delay</span>
                  </div>
                </div>
              </div>
            </div>
          ` : ''}

          <!-- 4. OPTIONAL DETAILS (Methodology Formula) -->
          <div class="p-4 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-600 flex items-start gap-2">
            <span class="material-symbols-outlined text-[16px] text-slate-400 shrink-0 mt-0.5">help_outline</span>
            <p>
              <strong>Reliability Score Formula:</strong> <code>Score = 0.50 × P_delay + 0.30 × N_delay + 0.20 × P_cancel</code>.
              Evaluates delay frequency at 50%, normalized delay magnitude at 30%, and route cancellation stability at 20%.
            </p>
          </div>
        </div>
      `;
    } catch (err) {
      container.innerHTML = `
        <div class="p-6 rounded-xl bg-red-50 border border-red-200 text-center space-y-3">
          <span class="material-symbols-outlined text-red-600 text-[32px]">error</span>
          <h3 class="text-base font-bold text-red-900">Analysis Error</h3>
          <p class="text-sm text-red-700">${err.message || 'Unable to connect to analytics engine.'}</p>
          <button onclick="document.getElementById('route-reliability-form').dispatchEvent(new Event('submit'))" class="px-4 py-2 bg-red-600 text-white rounded-lg font-medium text-sm hover:bg-red-700 transition-colors">
            Try Again
          </button>
        </div>
      `;
    }
  });
}

// --------------------------------------------------------------------------
// 4. SCREEN 4: HISTORICAL RISK ADVISOR
// --------------------------------------------------------------------------
function initRiskAdvisorScreen() {
  const form = document.getElementById('risk-advisor-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const airline = document.getElementById('ra-airline').value;
    const origin = document.getElementById('ra-origin').value;
    const dest = document.getElementById('ra-dest').value;
    const month = parseInt(document.getElementById('ra-month').value || '6', 10);
    const timeVal = document.getElementById('ra-time').value || 'EVENING';

    const container = document.getElementById('risk-advisor-results');
    if (!container) return;

    const displayMonth = MONTH_NAMES[month] || 'June';

    // Loading State
    container.innerHTML = `
      <div class="py-12 flex flex-col items-center justify-center text-center space-y-3">
        <div class="w-9 h-9 border-3 border-primary border-t-transparent rounded-full animate-spin"></div>
        <p class="text-sm text-slate-600 font-medium">Evaluating historical risk for ${airline} on ${origin} → ${dest} (${displayMonth})...</p>
      </div>
    `;
    container.classList.remove('hidden');

    try {
      const data = await AirFlightAPI.getDelayRisk(airline, origin, dest, month, timeVal);

      if (data.status === 'INSUFFICIENT_HISTORICAL_DATA') {
        const flightsCount = data.flights_found !== undefined ? data.flights_found : (data.sample_size || 0);
        container.innerHTML = `
          <div class="p-6 rounded-xl bg-white border border-slate-200 text-center space-y-2 shadow-sm">
            <span class="material-symbols-outlined text-slate-400 text-[32px]">info</span>
            <h3 class="text-base font-bold text-slate-900">Insufficient Historical Data</h3>
            <p class="text-sm text-slate-600 max-w-lg mx-auto">
              Only <strong>${flightsCount}</strong> flights documented for carrier <strong>${airline}</strong> on <strong>${origin} → ${dest}</strong> (minimum 10 required for historical risk evaluation).
            </p>
          </div>
        `;
        return;
      }

      if (data.status !== 'SUCCESS') {
        throw new Error(data.message || 'Unable to retrieve historical risk evaluation.');
      }

      // Map raw risk (LOW / MEDIUM / HIGH) to prompt specification (LOW / MODERATE / HIGH)
      const rawRisk = (data.historical_delay_risk || 'MEDIUM').toUpperCase();
      const displayRisk = (rawRisk === 'MEDIUM' || rawRisk === 'MODERATE') ? 'MODERATE' : rawRisk;
      const badge = getRiskBadge(displayRisk);
      const metrics = data.metrics || {};
      const patterns = data.observed_patterns || [];
      const betterAlts = data.better_historical_alternatives || [];
      const evaluatedMonth = MONTH_NAMES[data.month] || displayMonth;

      // Pattern: Input → Clear answer → 2–4 key metrics → One useful visualization → Optional details
      container.innerHTML = `
        <div class="space-y-6">
          <!-- 1. CLEAR ANSWER (RISK BANNER) -->
          <div class="p-6 rounded-xl border shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 ${badge.bg}">
            <div class="space-y-1.5">
              <div class="inline-flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider">
                <span class="w-2 h-2 rounded-full ${badge.dot}"></span>
                ${displayRisk} HISTORICAL RISK
              </div>
              <h2 class="text-2xl sm:text-3xl font-extrabold text-slate-900">
                ${airline} • ${origin} to ${dest} (${data.time_of_day || timeVal})
              </h2>
              <p class="text-sm text-slate-700 max-w-2xl">
                ${patterns.length > 0 ? patterns[0] : `Historical flights during this window exhibited ${displayRisk.toLowerCase()} disruption frequencies.`}
              </p>
            </div>
            <div class="sm:text-right shrink-0 bg-white/90 backdrop-blur-sm px-4 py-3 rounded-lg border border-slate-200/60 w-full sm:w-auto">
              <span class="text-xs text-slate-500 uppercase font-medium block">Sample Scope</span>
              <span class="text-base font-bold text-slate-900">${data.sample_scope === 'SPECIFIC_TIME_WINDOW' ? 'Exact Time Window' : 'Overall Route'}</span>
              <span class="text-xs text-slate-600 block mt-0.5">${evaluatedMonth} • <strong>${data.sample_size || 0}</strong> flights</span>
            </div>
          </div>

          <!-- 2. 4 KEY METRICS -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Delay Rate (&gt;15 min)</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold ${badge.textColor}">${metrics.delay_rate_pct || 0}%</div>
              <p class="mt-1.5 text-xs text-slate-500">${metrics.delayed_flights || 0} delayed flights</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Avg Arrival Delay</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-slate-900">+${metrics.avg_arrival_delay_min || 0} <span class="text-sm font-normal text-slate-500">min</span></div>
              <p class="mt-1.5 text-xs text-slate-500">Across observed profile</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Cancellation Rate</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-slate-900">${metrics.cancellation_rate_pct || 0}%</div>
              <p class="mt-1.5 text-xs text-slate-500">${metrics.cancelled_flights || 0} cancellations</p>
            </div>
            <div class="p-5 rounded-xl bg-white border border-slate-200 shadow-sm">
              <span class="text-xs text-slate-500 font-semibold uppercase tracking-wider block">Sample Size</span>
              <div class="mt-1 text-2xl sm:text-3xl font-extrabold text-slate-900">${data.sample_size || 0}</div>
              <p class="mt-1.5 text-xs text-slate-500">BTS 2015 historical logs</p>
            </div>
          </div>

          <!-- 3. ACTIONABLE COMPARISON: Selected vs Better Historical Alternative -->
          ${betterAlts.length > 0 ? `
            <div class="p-6 rounded-xl bg-white border border-slate-200 shadow-sm space-y-4">
              <div class="flex items-center justify-between">
                <div>
                  <h3 class="text-base font-bold text-slate-900">Better Historical Alternative Available</h3>
                  <p class="text-xs text-slate-500">A lower-disruption option identified in the historical flight record for this route.</p>
                </div>
                <span class="px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
                  Alternative Found
                </span>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- Current Selection -->
                <div class="p-4 rounded-lg bg-slate-50 border border-slate-200 space-y-2">
                  <span class="text-xs uppercase text-slate-500 font-semibold">Your Selected Profile</span>
                  <h4 class="text-lg font-bold text-slate-900">${airline} • ${data.time_of_day || timeVal}</h4>
                  <div class="flex items-center gap-3 text-xs sm:text-sm text-slate-700 font-medium pt-1">
                    <span>${metrics.delay_rate_pct}% Delay Rate</span>
                    <span>•</span>
                    <span>+${metrics.avg_arrival_delay_min}m Avg Delay</span>
                  </div>
                </div>

                <!-- Better Alternative -->
                <div class="p-4 rounded-lg bg-emerald-50 border border-emerald-200 space-y-2 flex flex-col justify-between">
                  <div class="space-y-1">
                    <span class="text-xs uppercase text-emerald-800 font-bold flex items-center gap-1">
                      <span class="material-symbols-outlined text-[14px]">recommend</span> Lower Historical Delay
                    </span>
                    <h4 class="text-lg font-bold text-emerald-950">${betterAlts[0].airline} • ${betterAlts[0].time_of_day}</h4>
                    <div class="flex items-center gap-3 text-xs sm:text-sm text-emerald-900 font-semibold pt-1">
                      <span>${betterAlts[0].delay_rate_pct}% Delay Rate</span>
                      <span>•</span>
                      <span>+${betterAlts[0].avg_arrival_delay_min}m Avg Delay</span>
                    </div>
                  </div>
                  <div class="pt-3">
                    <button type="button" onclick="switchToAlternative('${betterAlts[0].time_of_day}', '${betterAlts[0].airline}')"
                      class="px-4 py-2 bg-emerald-700 hover:bg-emerald-800 text-white rounded-lg text-xs font-semibold transition-colors flex items-center gap-1.5 shadow-sm">
                      <span>Switch to ${betterAlts[0].time_of_day}</span>
                      <span class="material-symbols-outlined text-[14px]">arrow_forward</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ` : ''}

          <!-- 4. OBSERVED OPERATIONAL DRIVERS -->
          ${patterns.length > 0 ? `
            <div class="p-5 rounded-xl bg-slate-50 border border-slate-200 space-y-2">
              <span class="text-xs text-slate-600 font-bold uppercase tracking-wider block">Observed Operational Drivers</span>
              <ul class="space-y-1.5 text-xs sm:text-sm text-slate-700">
                ${patterns.map(p => `
                  <li class="flex items-start gap-2">
                    <span class="material-symbols-outlined text-primary text-[16px] mt-0.5">check_circle</span>
                    <span>${p}</span>
                  </li>
                `).join('')}
              </ul>
            </div>
          ` : ''}
        </div>
      `;
    } catch (err) {
      container.innerHTML = `
        <div class="p-6 rounded-xl bg-red-50 border border-red-200 text-center space-y-3">
          <span class="material-symbols-outlined text-red-600 text-[32px]">error</span>
          <h3 class="text-base font-bold text-red-900">Evaluation Error</h3>
          <p class="text-sm text-red-700">${err.message || 'Unable to connect to analytics engine.'}</p>
          <button onclick="document.getElementById('risk-advisor-form').dispatchEvent(new Event('submit'))" class="px-4 py-2 bg-red-600 text-white rounded-lg font-medium text-sm hover:bg-red-700 transition-colors">
            Try Again
          </button>
        </div>
      `;
    }
  });
}

function switchToAlternative(period, airline) {
  if (period) {
    const timeSelect = document.getElementById('ra-time');
    if (timeSelect) {
      timeSelect.value = period;
    }
  }
  if (airline) {
    const airlineSelect = document.getElementById('ra-airline');
    if (airlineSelect) {
      airlineSelect.value = airline;
    }
  }
  const form = document.getElementById('risk-advisor-form');
  if (form) {
    form.dispatchEvent(new Event('submit'));
  }
}

window.switchToAlternative = switchToAlternative;
