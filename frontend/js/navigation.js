/**
 * AirFlight Client-Side Router & Navigation Controller
 * Handles routing between Home, Best Time to Fly, Route Reliability, and Historical Risk Advisor.
 */

const ROUTES = {
  'home': {
    title: 'AirFlight — Travel Decision Support',
    viewId: 'view-home'
  },
  'best-time-to-fly': {
    title: 'Best Time to Fly — AirFlight',
    viewId: 'view-best-time-to-fly'
  },
  'route-reliability': {
    title: 'Route Reliability — AirFlight',
    viewId: 'view-route-reliability'
  },
  'historical-risk-advisor': {
    title: 'Historical Risk Advisor — AirFlight',
    viewId: 'view-historical-risk-advisor'
  }
};

function toggleMobileMenu() {
  const menu = document.getElementById('mobile-menu');
  if (menu) {
    menu.classList.toggle('hidden');
  }
}

function navigateTo(routeKey) {
  const targetKey = ROUTES[routeKey] ? routeKey : 'home';
  const route = ROUTES[targetKey];

  // 1. Toggle active view container
  document.querySelectorAll('.page-view').forEach(view => {
    view.classList.remove('active');
  });

  const activeView = document.getElementById(route.viewId);
  if (activeView) {
    activeView.classList.add('active');
  }

  // 2. Sync active classes on desktop navigation links
  const desktopLinks = document.querySelectorAll('#desktop-nav a');
  desktopLinks.forEach(link => {
    const linkPath = link.getAttribute('data-path');
    if (linkPath === targetKey) {
      link.className = 'px-3.5 py-1.5 rounded-lg text-sm font-semibold transition-colors bg-primary text-white shadow-sm';
      link.setAttribute('aria-current', 'page');
    } else {
      link.className = 'px-3.5 py-1.5 rounded-lg text-sm font-semibold transition-colors text-slate-600 hover:bg-slate-100 hover:text-slate-900';
      link.removeAttribute('aria-current');
    }
  });

  // 3. Sync active classes on mobile menu links
  const mobileLinks = document.querySelectorAll('#mobile-menu a[data-path]');
  mobileLinks.forEach(link => {
    const linkPath = link.getAttribute('data-path');
    if (linkPath === targetKey) {
      link.className = 'block px-3.5 py-2 rounded-lg text-sm font-semibold bg-primary text-white';
      link.setAttribute('aria-current', 'page');
    } else {
      link.className = 'block px-3.5 py-2 rounded-lg text-sm font-semibold text-slate-600 hover:bg-slate-100 hover:text-slate-900';
      link.removeAttribute('aria-current');
    }
  });

  // 4. Update document title
  document.title = route.title;

  // 5. Sync URL hash without page reload
  if (window.location.hash !== '#' + targetKey) {
    history.pushState(null, '', '#' + targetKey);
  }

  // 6. Scroll smoothly to top
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Window Event Listeners
window.addEventListener('hashchange', () => {
  const hash = window.location.hash.replace('#', '');
  if (hash) {
    navigateTo(hash);
  } else {
    navigateTo('home');
  }
});

document.addEventListener('DOMContentLoaded', () => {
  const initialHash = window.location.hash.replace('#', '');
  if (initialHash && ROUTES[initialHash]) {
    navigateTo(initialHash);
  } else {
    navigateTo('home');
  }
});

window.navigateTo = navigateTo;
window.toggleMobileMenu = toggleMobileMenu;
