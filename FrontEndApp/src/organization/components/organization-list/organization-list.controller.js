export default class OrganizationListCtrl{
    constructor($scope, $http, $routeParams) {
        "ngInject";
        console.log($routeParams);
        this.pageNum = 'pageNum' in $routeParams ? $routeParams['pageNum'] : '1'; 
        this.pageSize = 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25';
        this.searchString = $routeParams['search'];
        $http.get('api/organization.json').then((response) => {
            console.log(response);
            this.organizationList = response.data.list;
            this.total = response.data.total;
            if (this.pageNum != response.data.page) alert('API returned wrong page');
            if (this.pageSize != response.data.pageSize) alert('API returned wrong page size');
            this.pageShowCnt = Math.ceil(this.total/this.pageSize);
        });    
    }
}