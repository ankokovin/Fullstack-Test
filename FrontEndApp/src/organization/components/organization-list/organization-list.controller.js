export default class OrganizationListCtrl{
    constructor($scope, $http, $routeParams) {
        "ngInject";
        this.params = {
            page : 'page' in $routeParams ? $routeParams['page'] : '1',
            pageSize : 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25',
            searchName : $routeParams['search']
        }
        console.log(this.params);

        $http.get('api/organization',{ params: this.params }).then((response) => {
            console.log(response);
            this.organizationList = response.data.list;
            this.total = response.data.total;
            if (this.params.page != response.data.page) alert('API returned wrong page');
            if (this.params.pageSize != response.data.pageSize) alert('API returned wrong page size');
            this.pageShowCnt = Array(this.total/this.params.pageSize);
        });    
    }
}