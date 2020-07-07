export default class OrganizationListTreeCtrl{
    constructor(OrganizationService) {
        "ngInject";
        this.OrganizationService = OrganizationService;
        
    }

    $onInit() {
        "ngInject";
        this.OrganizationService.get_node().then((data) => {
            this.children = data.children;
            console.log(this.children);
        });
    }    
    
}