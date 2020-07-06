export default class OrganizationListCtrl{
    constructor($scope, $http, $routeParams) {
        "ngInject";
        console.log($routeParams);
        this.params = {
            pageNum : 'pageNum' in $routeParams ? $routeParams['pageNum'] : '1',
            pageSize : 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25',
            searchName : $routeParams['search']
        }
        $http.get('api/organization',{ params: this.params }).then((response) => {
            console.log(response);
            this.organizationList = response.data;
        });    
    }
}