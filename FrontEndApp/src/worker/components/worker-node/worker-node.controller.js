export default class WorkerListCtrl{
    constructor(WorkerService) {
        "ngInject";
        this.WorkerService = WorkerService;
        this.load = false;
        this.visible = false;
        this.childrenVisible = false;
    }

    $onChanges(changesObj) {
        console.log(changesObj);
        if (changesObj.node) {
            this.node = changesObj.node.currentValue;
            this.id = this.node.item.id;
            this.name = this.node.item.name;
            this.org_id =  this.node.item.org_id;
            this.org_name =  this.node.item.org_name;
            this.children = this.node.children;
        }
        if (changesObj.load) {
            if (!this.load) {
                this.load =  this.load || changesObj.load.currentValue;
                if (this.load) this.load_children();
            }
        }
        if (changesObj.childrenVisible) {
            this.childrenVisible = changesObj.childrenVisible.currentValue;
            this.$onChanges({
                load: {
                    previousValue: this.load,
                    currentValue: this.childrenVisible
                }
            })
        }
        console.log(this.children);
    }

    load_children() {
        this.WorkerService.get_node(this.id).then((data) => {
            this.id = data.item.id;
            this.name = data.item.name;
            this.org_id = data.item.org_id;
            this.org_name = data.item.org_name;
            console.log(this.org_id , this.org_name);
            this.children = data.children;
        });    
    }

    unfold(e){
        if (this.children.length) this.$onChanges({
            childrenVisible: {
                previousValue: this.childrenVisible,
                currentValue: !this.childrenVisible
            }
        });
    }
}