export default class OrganizationListCtrl{
    constructor(OrganizationService) {
        "ngInject";
        this.OrganizationService = OrganizationService;
        this.load = false;
        this.visible = false;
        this.childrenVisible = false;
    }

    $onChanges(changesObj) {
        if (changesObj.node) {
            this.id = this.node.item.id;
            this.name = this.node.item.orgName;
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
    }

    load_children() {
        this.OrganizationService.get_node(this.id).then((data) => {
            this.id = data.item.id;
            this.name = data.item.orgName;
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