import WorkerItemTemplate from './worker-item.template.html';

let workerItemComponent = {
    componentName: 'workerItemComponent',
    template: WorkerItemTemplate,
    controller: function LoadworkerListTree() {
        console.log('LoadworkerList')
    }
}

export default workerItemComponent;