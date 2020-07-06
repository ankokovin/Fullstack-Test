import WorkerListTreeTemplate from './worker-list-tree.template.html';

let workerListTreeComponent = {
    componentName: 'workerListTreeComponent',
    template: WorkerListTreeTemplate,
    controller: function LoadworkerListTree() {
        console.log('LoadworkerList')
    }
}

export default workerListTreeComponent;