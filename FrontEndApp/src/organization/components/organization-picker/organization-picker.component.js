import OrganizationPickerTemplate from './organization-picker.template.html';
import OrganizationPickerCtrl from './organization-picker.controller';

let OrganizationPickerComponent = {
    componentName: 'organizationPickerComponent',
    template: OrganizationPickerTemplate,
    controller: OrganizationPickerCtrl,
    bindings: {
        message: '@',
        headid: '='
    }
}

export default OrganizationPickerComponent;