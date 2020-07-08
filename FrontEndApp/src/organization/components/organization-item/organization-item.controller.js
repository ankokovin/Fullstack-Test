export default class OrganizationItemCtrl{
    constructor($routeParams, OrganizationService, $location) {
        "ngInject";
        this.new =  'id' in $routeParams;
        this.$location = $location;
        this.OrganizationService = OrganizationService;
        this.id =  $routeParams.id;
    }

    $onInit() {
        if (this.new) {
            this.OrganizationService.get(this.id)
            .then((data)=>{
                this.name = data.orgName;
                this.head_id = data.headOrgId;
            }, (error) => {
                if (error.status === 404) {
                    this.$location.path('/not-found');
                }
            });    
        }
    }

    create() {
        console.log(this.name, this.head_id);
        this.OrganizationService.create(this.name, this.head_id).then(
            (response) => console.log('Nice') //TODO: show some message, redirect?
        );
    }
}