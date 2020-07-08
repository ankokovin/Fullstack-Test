export default class WorkerListCtrl{
    constructor(WorkerService, $routeParams) {
        "ngInject";
        this.page = 'page' in $routeParams ? $routeParams['page'] : '1';
        this.pageSize = 'pageSize' in $routeParams ? $routeParams['pageSize'] : '25';
        this.searchName = $routeParams['search'];
        this.searchOrgName = $routeParams['searchOrg'];
        WorkerService.get_paged(this.page, this.pageSize, this.searchName, this.searchOrgName).then((data) => {
            this.workerList = data.list;
            this.total = data.total;
            this.pageShowCnt = Array(Math.ceil(this.total/this.pageSize));
        });    
    }
}