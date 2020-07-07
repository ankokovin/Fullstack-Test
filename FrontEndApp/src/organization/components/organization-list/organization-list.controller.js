export default class OrganizationListCtrl{
    constructor(OrganizationService, $routeParams) {
        "ngInject";
        this.page = 'page' in $routeParams ? $routeParams['page'] : '1';
        this.pageSize = 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25';
        this.searchName = $routeParams['search'];
        
        OrganizationService.get_paged(this.page, this.pageSize, this.searchName).then((data) => {
            this.organizationList = data.list;
            this.total = data.total;
            this.pageShowCnt = Array(Math.ceil(this.total/this.pageSize));
        });    
    }
}