export default function OrganizationListCtrl($scope, $http, $routeParams) {
    console.log($routeParams);
    let pageNum = 'pageNum' in $routeParams ? $routeParams['pageNum'] : '1'; 
    let pageSize = 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25';
    let searchString = $routeParams['search'];
    $http.get('api/organization.json').then((response) => {
        console.log(response);
        $scope.organizationList = response.data;
    });    
}