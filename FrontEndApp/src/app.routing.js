import 'angular-route';
import OrganizationRouting from './organization/organization.routing';
import WorkerRouting from './worker/worker.routing';
let routingConfig = function config($routeProvider) {
  "ngInject";
  WorkerRouting($routeProvider);
  OrganizationRouting($routeProvider);
  $routeProvider.when('/', {
    template: '<organization-list-component></organization-list-component>'
  }).when('/not-found', {
    template: '<h1>404</h1>'
  }).otherwise('/not-found');
};

export default routingConfig;