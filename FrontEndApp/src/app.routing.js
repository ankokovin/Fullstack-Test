import 'angular-route';
let routingConfig = function config($routeProvider) {
  $routeProvider.when('/', {
    template: '<organization-list-component></organization-list-component>'
  });
  $routeProvider.when('/organization/list', {
    template: '<organization-list-component></organization-list-component>'
  });
  $routeProvider.when('/organization/tree_list', {
    template: '<organization-list-tree-component></organization-list-tree-component>'
  });
  $routeProvider.when('/organization/create', {
    template: '<organization-create-component></organization-create-component>'
  });
  $routeProvider.when('/worker/list', {
    template: '<worker-list-component></worker-list-component>'
  });
  $routeProvider.when('/worker/tree_list', {
    template: '<worker-list-tree-component></worker-list-tree-component>'
  });
  $routeProvider.when('/worker/create', {
    template: '<worker-create-component></worker-create-component>'
  });
  $routeProvider.when('/not-found', {
    template: '<h1>404</h1>'
  });
  $routeProvider.otherwise('/not-found');
};

export default routingConfig;