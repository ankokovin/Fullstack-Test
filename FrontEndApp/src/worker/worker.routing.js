export default function($routeProvider){
    return $routeProvider.when('/worker/list', {
        template: '<worker-list-component></worker-list-component>'
      }).when('/worker/tree-list', {
        template: '<worker-list-tree-component></worker-list-tree-component>'
      }).when('/worker/item', {
        template: '<worker-item-component></worker-item-component>'
      }).when('/worker/item/:id', {
        template: '<worker-item-component></worker-item-component>'
      });
}