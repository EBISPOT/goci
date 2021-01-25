class DiseaseTrait {

    static getTraitData(pageNumber) {
        const URI = `/api/disease-traits?page=${--pageNumber}`;
        let httpRequest = HttpRequestEngine.requestWithoutBody(URI, 'GET');
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            // UI.hideRow('loader-row');
            UI.removeChildren('data-list');
            let columns = ['trait'];
            data._embedded.diseaseTraits.map(data => {
                DiseaseTrait.updateDataRows(data, data.id, columns, 'data-list');
            });
            DiseaseTrait.generatePagination(data.page);
        });
    }

    static updateDataRows(dataObject, dataId, properties, tableBodyId) {
        const tableRows = document.querySelector(`#${tableBodyId}`);
        const tr = document.createElement('tr');
        tr.setAttribute('id', `row-${dataId}`);
        //blink
        properties.forEach((property, index, properties) => {
            let td = UI.tableData(dataObject[property], `col-${index}-${dataId}`);
            td.setAttribute("class", "ui-menu-open");
            tr.appendChild(td);
            if (Object.is(properties.length - 1, index)) {
                td = document.createElement("td");
                let deleteButton = UI.buttonElement('Delete',`deleteBtn-${dataId}`, 'fa-trash-alt');
                td.appendChild(deleteButton);
                tr.appendChild(td);
            }
        });
        tableRows.prepend(tr);

        // Delete Event : Event Listener delete Button in the last column
        document.querySelector(`#deleteBtn-${dataId}`).addEventListener('click', function () {
            let uri = `/api/disease-traits/${dataId}`;
            let httpRequest = HttpRequestEngine.requestWithoutBody(uri, 'DELETE');
            HttpRequestEngine.fetchRequest(httpRequest).then((result) => {
                let row = document.querySelector(`#row-${dataId}`);
                row.remove();
            });
        });

        NavDrawer.init();
    }

    static generatePagination(pageData){

        let size = pageData.size;
        let totalPages = pageData.totalPages;
        let totalElements = pageData.totalElements;
        let page = pageData.number + 1;
        let currentIndex = pageData.number + 1;
        let beginIndex = Math.max(1, page - 4);
        let endIndex = Math.min(beginIndex + 7, totalPages);

        let pageList = document.createElement('ul');
        pageList.setAttribute('class', 'pagination pagination-sm');

        // First Pagination Link
        let firstPage = document.createElement("li");
        let firstLink = document.createElement("a");
        let firstText = document.createTextNode('First «');
        if (currentIndex === 1){
            firstPage.setAttribute('class', 'disabled');
        }else {
            firstLink.style.cursor = "pointer";
            firstLink.addEventListener("click",  ()=>{
                DiseaseTrait.getTraitData(1);
            });
        }
        firstLink.appendChild(firstText);
        firstPage.appendChild(firstLink);
        pageList.appendChild(firstPage);

        // Next Pagination Links:
        for (let i=beginIndex; i<endIndex; i++ ){
            let nextPage = document.createElement("li");
            let nextLink = document.createElement("a");
            let nextPageText = document.createTextNode(i);
            if (i === page){
                nextPage.setAttribute('class', 'active');
            }else {
                nextLink.style.cursor = "pointer";
                nextLink.addEventListener("click",  ()=>{
                    DiseaseTrait.getTraitData(i);
                });
            }
            nextLink.appendChild(nextPageText);
            nextPage.appendChild(nextLink);
            pageList.appendChild(nextPage);
        }

        // Last Pagination Link:
        //let pageId = 'finalPage';
        let finalPage = document.createElement("li");
        let finalPageLink = document.createElement("a");
        finalPageLink.style.cursor = "pointer";
        //finalPageLink.setAttribute('id', pageId);
        let finalPageText = document.createTextNode('Last «');
        if (page === totalPages){
            finalPage.setAttribute('class', 'disabled');
        }else {
            finalPageLink.addEventListener("click",  ()=>{
                DiseaseTrait.getTraitData(totalPages);
            });
        }
        finalPageLink.appendChild(finalPageText);
        finalPage.appendChild(finalPageLink);
        pageList.appendChild(finalPage);

        UI.removeChildren('page-area');
        const dPagination = document.querySelector('#page-area');
        dPagination.appendChild(pageList);
    }

    static floatingActionButtonEvents(){
        const CLICK_EVENT = 'click';
        document.querySelector('#activate-analysis-form-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('analysis-form-view');
        });
        document.querySelector('#activate-upload-form-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('upload-form-view');
        });
        document.querySelector('#activate-add-form-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('add-form-view');
        });
        document.querySelector('#activate-visualization-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('visualization-view');
        });
        document.querySelector('#activate-trait-table-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('trait-table-view');
        });
    }

    static switchFormView(selectedViewId) {
        let views = ["add-form-view", "upload-form-view", "analysis-form-view", "visualization-view", "trait-table-view"];
        views.map(view => {
            UI.hideRow(`${view}`);
        });
        UI.unHideRow(`${selectedViewId}`);
    }


}

