import 'angular-route';
let config = [
    ['$routeProvider',function config($routeProvider) {
        $routeProvider.
          when('/', {
            template: '<organization-list-component></organization-list-component>'
          }).
          when('/organization/list', {
            template: '<organization-list-component></organization-list-component>'
          }).
          when('/not-found'), {
            template: '<h1>404</h1>'
          }
          otherwise('/not-found');
      }
    ]
];

export default config;