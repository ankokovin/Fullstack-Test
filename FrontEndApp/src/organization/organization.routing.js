export default function($routeProvider){
    return $routeProvider.when('/organization/list', {
        template: '<organization-list-component></organization-list-component>'
      }).when('/organization/tree-list', {
        template: '<organization-list-tree-component></organization-list-tree-component>'
      }).when('/organization/item/', {
        template: '<organization-item-component></organization-item-component>'
      }).when('/organization/item/:id',{
        template: '<organization-item-component></organization-item-component>'
      });
}