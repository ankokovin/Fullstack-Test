export default class OrganizationService {
    constructor($http) {
        "ngInject";
        this.$http = $http;
    }

    get_node(id) {
        return new Promise((resolve, reject) => {
            this.$http.get('api/organization/tree',
            {
                params:{
                    id: id
                }
            })
            .then((response) => {
                console.log(response);
                resolve(response.data);
            },(error) => {
                reject(error);
            });
        });
    }

    get_paged(page, pageSize, searchName) {
        return new Promise((resolve, reject) =>{
            this.$http.get('api/organization',{ 
                params: {
                    page:page,
                    pageSize:pageSize,
                    searchName:searchName,
                }
            }).then(
                (response) => {
                    var errors = [];
                    if (response.data.page != page) errors.push('api returned wrong page');
                    if (response.data.pageSize != pageSize) errors.push('api returned wrong pageSize');
                    if (errors.length) reject(errors);
                    resolve(response.data);
                },
                (error) => reject(error)
            )
        });
    }
}
