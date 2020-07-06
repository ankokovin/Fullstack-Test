export default class OrganizationListTreeCtrl{
    constructor($http) {
        "ngInject";
        this.http = $http;
    }

    $onInit() {
        "ngInject";
        this.http.get('api/organization-tree.json').then((response) => {
            console.log(response);
            this.children = response.data['children'];
            console.log(this.children)
        });
    }    
    
}