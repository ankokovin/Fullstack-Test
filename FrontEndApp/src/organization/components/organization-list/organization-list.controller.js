export default function OrganizationListCtrl($scope, $http, $routeParams) {
    "ngInject";
    console.log($routeParams);
    let params = {
        pageNum : 'pageNum' in $routeParams ? $routeParams['pageNum'] : '1',
        pageSize : 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25',
        searchString : $routeParams['search']
    }
    $http.get('api/organization',{ params:params }).then((response) => {
        console.log(response);
        $scope.organizationList = response.data;
    });    
}