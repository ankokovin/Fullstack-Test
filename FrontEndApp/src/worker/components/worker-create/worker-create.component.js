import WorkerCreateTemplate from './worker-create.template.html';

let workerCreateComponent = {
    componentName: 'workerCreateComponent',
    template: WorkerCreateTemplate,
    controller: function LoadworkerListTree() {
        console.log('LoadworkerList')
    }
}

export default workerCreateComponent;