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

    load() {
        //TODO: repeated code from organization-list: make a service
        let params = {
            page: 1,
            pageSize: 5,
            orgName: this.organization.orgName
        };
        console.log(params)
        this.http.get('api/organization.json',{ params: params }).then((response) => {
            console.log(response);
            this.scope.organizationList = response.data.list.slice(0,5);
            if (response.data.page != 1) console.log('API returned wrong page');
            if (response.data.pageSize != 10) console.log('API returned wrong page size');
            
        });  
    }
}