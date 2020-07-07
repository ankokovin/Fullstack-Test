export default class OrganizationListCtrl{
    constructor(OrganizationService, $routeParams) {
        "ngInject";
        this.page = 'page' in $routeParams ? $routeParams['page'] : '1';
        this.pageSize = 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25';
        this.searchName = $routeParams['search'];
        
        OrganizationService.get_paged(this.page, this.pagaSize, this.searchName).then((data) => {
            this.organizationList = response.data.list;
            this.total = response.data.total;
            this.pageShowCnt = Array(this.total/this.params.pageSize);
        });    
    }
}