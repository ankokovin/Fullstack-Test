import OrganizationCreateTemplate from './organization-create.template.html';

let OrganizationCreateComponent = {
    componentName: 'organizationCreateComponent',
    template: OrganizationCreateTemplate,
    controller: function LoadOrganizationListTree() {
        console.log('LoadOrganizationList')
    }
}

export default OrganizationCreateComponent;