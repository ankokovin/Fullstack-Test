const { default: organizationListModule } = require("./organization-list.module");

let organizationListComponent = {
    componentName: 'organizationListComponent',
    template: '<h2>I\'m a list of organizations!</h2>',
    controller: function LoadOrganizationList() {
        console.log('LoadOrganizationList')
    }
};

export default organizationListComponent;