/**
 * AirFlight API Client
 * Connects frontend views directly to the AirFlight REST API.
 * Never invents, fakes, or mocks flight metrics.
 */

const API_BASE = '/api';

const AirFlightAPI = {
  /**
   * Fetch common airports for dropdowns
   */
  async getAirports() {
    try {
      const res = await fetch(`${API_BASE}/airports`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return await res.json();
    } catch (err) {
      console.warn('Failed to load airports from API:', err);
      return { status: 'ERROR', message: err.message };
    }
  },

  /**
   * Fetch available airlines
   */
  async getAirlines() {
    try {
      const res = await fetch(`${API_BASE}/airlines`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return await res.json();
    } catch (err) {
      console.warn('Failed to load airlines from API:', err);
      return { status: 'ERROR', message: err.message };
    }
  },

  /**
   * Evaluate Best Time to Fly for an origin and destination
   * @param {string} origin
   * @param {string} dest
   * @param {number|string|null} month
   */
  async getBestTime(origin, dest, month = null) {
    const params = new URLSearchParams({
      origin: origin.toUpperCase().trim(),
      dest: dest.toUpperCase().trim()
    });
    if (month !== null && month !== undefined && month !== '' && month !== 'all') {
      params.append('month', month);
    } else {
      params.append('month', 'all');
    }

    const res = await fetch(`${API_BASE}/best-time?${params.toString()}`);
    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      throw new Error(errData.message || `API error (${res.status})`);
    }
    return await res.json();
  },

  /**
   * Compute Route Reliability Score
   * @param {string} origin
   * @param {string} dest
   */
  async getRouteReliability(origin, dest) {
    const params = new URLSearchParams({
      origin: origin.toUpperCase().trim(),
      dest: dest.toUpperCase().trim()
    });

    const res = await fetch(`${API_BASE}/route-reliability?${params.toString()}`);
    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      throw new Error(errData.message || `API error (${res.status})`);
    }
    return await res.json();
  },

  /**
   * Evaluate Historical Delay Risk
   * @param {string} airline
   * @param {string} origin
   * @param {string} dest
   * @param {number|string} month
   * @param {string|number} time
   */
  async getDelayRisk(airline, origin, dest, month, time) {
    const params = new URLSearchParams({
      airline: airline.toUpperCase().trim(),
      origin: origin.toUpperCase().trim(),
      dest: dest.toUpperCase().trim(),
      month: month,
      time: time
    });

    const res = await fetch(`${API_BASE}/delay-risk?${params.toString()}`);
    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      throw new Error(errData.message || `API error (${res.status})`);
    }
    return await res.json();
  }
};

window.AirFlightAPI = AirFlightAPI;
