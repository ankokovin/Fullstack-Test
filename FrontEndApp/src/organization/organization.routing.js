export default function($routeProvider){
    return $routeProvider.when('/organization/list', {
        template: '<organization-list-component></organization-list-component>'
      }).when('/organization/tree-list', {
        template: '<organization-list-tree-component></organization-list-tree-component>'
      }).when('/organization/create', {
        template: '<organization-create-component></organization-create-component>'
      });
}