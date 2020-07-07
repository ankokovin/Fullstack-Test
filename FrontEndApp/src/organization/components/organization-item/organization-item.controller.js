export default class OrganizationItemCtrl{
    constructor($scope, $routeParams, OrganizationService) {
        "ngInject";
        this.new =  'id' in $routeParams;
        this.scope = $scope;
        this.OrganizationService = OrganizationService;
    }

    create() {
        console.log(this.name, this.head_id);
        this.OrganizationService.create(this.name, this.head_id).then(
            (response) => console.log('Nice') //TODO: show some message, redirect?
        );
    }
}