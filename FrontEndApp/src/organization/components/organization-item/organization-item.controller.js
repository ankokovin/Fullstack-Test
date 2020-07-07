export default class OrganizationItemCtrl{
    constructor($scope, $http, $routeParams) {
        "ngInject";
        this.organization = {};
        console.log(this.organization.id);
        this.new =  'id' in $routeParams;
        this.http = $http;
        this.scope = $scope;
        this.load();
    }
    select(id) {
        console.log(id);
        this.organization.head_id = id;
        this.searchName = this.scope.organizationList[id];
    }

    load() {
        //TODO: repeated code from organization-list: make a service
        let params = {
            page: 1,
            pageSize: 5,
            orgName: this.searchName
        };
        console.log(params)
        this.http.get('api/organization.json',{ params: params }).then((response) => {
            console.log(response);
            this.scope.organizationList = Object.fromEntries(response.data.list.slice(0,5).map(x=>[x.id, x.name]));
            console.log(this.scope.organizationList);
            if (response.data.page != 1) console.log('API returned wrong page');
            if (response.data.pageSize != 10) console.log('API returned wrong page size');
            this.organization.head_id = Object.keys(this.scope.organizationList).find(key => this.scope.organizationList[key] === this.searchName);
        });  
    }
}