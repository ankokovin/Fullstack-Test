export default function($routeProvider){
    return $routeProvider.when('/worker/list', {
        template: '<worker-list-component></worker-list-component>'
      }).when('/worker/tree_list', {
        template: '<worker-list-tree-component></worker-list-tree-component>'
      }).when('/worker/create', {
        template: '<worker-create-component></worker-create-component>'
      });
}