export default class OrganizationPickerCtrl {
    constructor(OrganizationService, $scope) {
        "ngInject";
        this.OrganizationService = OrganizationService;
        this.$scope = $scope;
        this.showList = false;
        this.error_ids = new Set();
    }

    $onChanges(changesObj) {
        console.log(changesObj);
        if (changesObj.message){
            this.message = changesObj.message.currentValue;
        }
        if (changesObj.errorid){
            if(changesObj.errorid.currentValue) this.error_ids.add(changesObj.errorid.currentValue.toString());
        }
        if (changesObj.errormsg){
            this.error_msg = changesObj.errormsg.currentValue;
        }
    }

    select(id) {
        console.log(id);
        this.headid = id;
        this.searchName = this.$scope.organizationList[id];
    }

    load() {
        console.log(this.$scope.organizationList);
        console.log(this.searchName);
        this.OrganizationService.get_paged(1, 5, this.searchName).then((data) => {
            console.log(data);
            this.$scope.organizationList = Object.fromEntries(data.list.slice(0,5).map(x=>[x.id, x.name]));
            console.log(this.$scope.organizationList);
            this.headid = Object.keys(this.$scope.organizationList).find(key => this.$scope.organizationList[key] === this.searchName);
            this.$scope.$apply();
        });  
    }

    searchNameFocus() {
        this.showList = true;
        this.load();
    }

    searchNameBlur(event) {
        console.log(event);
        this.showList = false;
    }


    listItemClass(id) {
        if (this.error_ids.has(id)) return 'disabled';
        else if (id === this.headid) return 'active';
        return ''
    }


}