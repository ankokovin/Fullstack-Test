export default class OrganizationPickerCtrl {
    constructor(OrganizationService, $scope) {
        "ngInject";
        this.OrganizationService = OrganizationService;
        this.$scope = $scope;
        this.showList = false;
        this.error_ids = new Set();
    }

    loadCur() {
        if (this.headid) {
            this.OrganizationService.get(this.headid).then((data) => {
                this.searchName = data.orgName;
                console.log(this.searchName);
                document.getElementById('search-name-field').value = this.searchName;
            })
        }
    }

    $onChanges(changesObj) {
        if (changesObj.message){
            this.message = changesObj.message.currentValue;
        }
        if (changesObj.errorid){
            if(changesObj.errorid.currentValue) this.error_ids.add(changesObj.errorid.currentValue.toString());
        }
        if (changesObj.errormsg){
            this.error_msg = changesObj.errormsg.currentValue;
        }
        if (changesObj.init){
            this.headid = changesObj.init.currentValue;
            this.loadCur();
        }
    }

    select(id) {
        this.headid = id;
        this.searchName = this.$scope.organizationList[id];
        this.showList = false;
    }

    load() {
        this.OrganizationService.get_paged(1, 5, this.searchName).then((data) => {
            this.$scope.organizationList = Object.fromEntries(data.list.slice(0,5).map(x=>[x.id, x.name]));
            this.headid = Object.keys(this.$scope.organizationList).find(key => this.$scope.organizationList[key] === this.searchName);
            this.$scope.$apply();
        });  
    }

    searchNameFocus() {
        this.showList = true;
        this.load();
    }

    searchNameBlur(event) {
        this.showList = false;
    }


    listItemClass(id) {
        if (this.error_ids.has(id)) return 'disabled';
        else if (id === this.headid) return 'active';
        return ''
    }

    inputClass() {
        if (this.headid) {
            if (this.error_ids.has(this.headid.toString())) return 'is-invalid';
            return 'is-valid';
        }
        return '';
    }


}