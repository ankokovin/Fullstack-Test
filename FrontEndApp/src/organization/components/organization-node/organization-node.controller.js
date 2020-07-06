export default class OrganizationListCtrl{
    constructor($http) {
        "ngInject";
        console.log('called node constructor');
        this.http = $http;
        this.load = false;
        this.visible = false;
        this.childrenVisible = false;
    }

    $onChanges(changesObj) {
        console.log(changesObj);
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
        console.log(this.children);
    }

    load_children(){

        this.http.get('api/organization-tree.json').then((response) => {
            console.log(response);
            this.id = response.data.item.id;
            this.name = response.data.item.orgName;
            this.children = response.data.children;
        });    
    }

    unfold(e){
        this.$onChanges({
            childrenVisible: {
                previousValue: this.childrenVisible,
                currentValue: !this.childrenVisible
            }
        })
    }
}