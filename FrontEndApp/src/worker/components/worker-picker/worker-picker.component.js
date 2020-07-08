import WorkerPickerTemplate from './worker-picker.template.html';
import WorkerPickerCtrl from './worker-picker.controller';

let WorkerPickerComponent = {
    componentName: 'workerPickerComponent',
    template: WorkerPickerTemplate,
    controller: WorkerPickerCtrl,
    bindings: {
        message: '@',
        headid: '=',
        errormsg: '<',
        errorid: '<',
        init: '<',
        required: '<',
        org_id: '<'
    }
}

export default WorkerPickerComponent;