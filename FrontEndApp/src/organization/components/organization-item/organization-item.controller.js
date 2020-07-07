export default class OrganizationItemCtrl{
    constructor($scope, $routeParams, OrganizationService) {
        "ngInject";
        this.organization = {};
        console.log(this.organization.id);
        this.new =  'id' in $routeParams;
        this.scope = $scope;
        this.OrganizationService = OrganizationService;
        this.showList = false;
    }
    select(id) {
        console.log(id);
        this.organization.head_id = id;
        this.searchName = this.scope.organizationList[id];
    }

    load() {
        console.log(this.scope.organizationList);
        console.log(this.searchName);
        this.OrganizationService.get_paged(1, 5, this.searchName).then((data) => {
            console.log(data);
            this.scope.organizationList = Object.fromEntries(data.list.slice(0,5).map(x=>[x.id, x.name]));
            console.log(this.scope.organizationList);
            this.organization.head_id = Object.keys(this.scope.organizationList).find(key => this.scope.organizationList[key] === this.searchName);
            this.scope.$apply();
        });  
    }

    create() {
        console.log(this.organization);
        this.OrganizationService.create(this.organization.name, this.organization.head_id).then(
            (response) => console.log('Nice') //TODO: show some message, redirect?
        );
    }

    searchNameFocus() {
        this.showList = true;
        this.load();
    }

    searchNameBlur(event) {
        console.log(event);
        this.showList = false;
    }
}