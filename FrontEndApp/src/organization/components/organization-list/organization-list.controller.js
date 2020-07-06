export default function OrganizationListCtrl($scope, $http) {
    $http.get('api/organization.json').then((response) => {
        console.log(response);
        $scope.organizationList = response.data;
    });    
}