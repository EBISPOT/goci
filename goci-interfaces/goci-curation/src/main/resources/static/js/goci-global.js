class UI {

    static clearFields(fieldArray) {
        fieldArray.forEach((field) => {
            document.querySelector(`#${field}`).value = '';
        })
    }

    static removeRow(rowId) {
        let row = document.querySelector(`#${rowId}`);
        row.parentNode.removeChild(row);
    }

    static hideRow(rowId){
        document.querySelector(`#${rowId}`).classList.add("hidden");
    }

    static unHideRow(rowId){
        document.querySelector(`#${rowId}`).classList.remove("hidden");
    }

    static removeChildren(parentId){
        let parentNode = document.querySelector(`#${parentId}`);
        while (parentNode.hasChildNodes()) {
            parentNode.removeChild(parentNode.firstChild);
        }
    }

    static hiddenField(name, defaultValue) {
        let hField = document.createElement('input');
        hField.setAttribute('type', 'hidden');
        hField.setAttribute('name', name);
        hField.setAttribute('value', defaultValue);
        return hField;
    }

    static editableTextfield(defaultValue, editorId, editorName) {
        let td = document.createElement("td");
        td.setAttribute('class', 'td-nobr');
        let textField = document.createElement('input');
        textField.setAttribute('type', 'text');
        textField.setAttribute('name', editorName);
        textField.setAttribute('class', `form-control tableFormi td-editable ${editorId}`);
        textField.setAttribute('style', 'border-bottom:none; color:#666; text-align:left; font-size:12px;');
        textField.setAttribute('value', defaultValue);

        //textField.readOnly = true;
        td.appendChild(textField);
        return td;
    }

    static tableData(text, dataId) {
        let td = document.createElement("td");
        td.setAttribute("id", dataId);
        td.setAttribute('data-title', text);
        text = (text === null) ? "NA" : text;
        let dText = document.createTextNode(text);

        if (text?.length > 30){
            td.setAttribute("class", "gwas-tooltip expand");
            let p = document.createElement("p");
            p.setAttribute('class', 'text-max-width-3rem');
            p.appendChild(dText);
            td.appendChild(p);
        }else {
            td.appendChild(dText);
        }


        return td;
    }

    static actionButton(text) {
        let btn = document.createElement("button");
        btn.setAttribute('type', 'button');
        btn.setAttribute('class', 'btn btn-default btn-xs');
        let btnText = document.createTextNode(text);
        btn.appendChild(btnText);
        return btn;
    }


    static buttonElement(text, btnId, iconFont) {
        let btn = document.createElement("button");
        btn.setAttribute('class', 'btn btn-sm btn-primary');
        btn.setAttribute('id', btnId);
        let deleteIcon = document.createElement("i");
        deleteIcon.setAttribute("class", `fas ${iconFont}`);
        let btnText = document.createTextNode(` \u00A0 ${text}`);
        btn.appendChild(deleteIcon);
        btn.appendChild(btnText);
        return btn;
    }

    static listButton(btnText, btnId, url, iconFont) {
        let list = document.createElement("li");
        let link = document.createElement("a");
        link.setAttribute('href', url);
        link.setAttribute('id', btnId);
        link.setAttribute('type', 'submit');
        let icon = document.createElement("i");
        icon.setAttribute('class', 'fa ' + iconFont);
        let dText = document.createTextNode(btnText);

        link.appendChild(icon);
        link.appendChild(dText);
        list.appendChild(link);
        return list;
    }

    static dropDownSelect(options){
        let selectTag = document.createElement('select');
        selectTag.setAttribute('class', 'form-control');
        options.map(option => {
            let selectOption = document.createElement('option');
            selectOption.setAttribute('value', option);
            let text = document.createTextNode(option);
            selectOption.appendChild(text);
            selectTag.appendChild(selectOption);
        });
        return selectTag;
    }

    static toggleButton() {
        let btn = document.createElement("button");
        btn.setAttribute('type', 'button');
        btn.setAttribute('class', 'btn btn-primary btn-xs dropdown-toggle');
        btn.setAttribute('data-toggle', 'dropdown');
        return btn;
    }

    static mySpan(dClass) {
        let span = document.createElement("span");
        span.setAttribute('class', dClass);
        return span;
    }


    static loadingIcon(){
        let tr = document.createElement("tr")
        tr.setAttribute("id", "loader-row");
        tr.setAttribute("style", "background-color: #FFFFFF;");
        let td = document.createElement("td");
        td.setAttribute("colspan", "2");
        let div = document.createElement("div");
        div.setAttribute("class", "loader");
        td.appendChild(div);
        tr.appendChild(td);
        return tr;
    }

    /*** TOAST ***/
    static launchToastNotification(message) {
        document.querySelector('#toast-message').innerHTML = message;
        document.querySelector('#toast').classList.add("show");
        setTimeout(() => {
            document.querySelector('#toast').classList.remove("show");
        }, 5000);
    }

    static clearText(valu) {

        let divTag = "replace"+valu;
        let rep = document.createElement("div");
        rep.setAttribute('class', 'styleFile');
        rep.setAttribute('style', 'color: green;');
    }

    static loadText(componentId, message, color, elementId) {
        let fakePath = document.getElementById(elementId).value;
        let fileName = fakePath.split("\\")[2];
        message = `Waiting ... ${message}: ${fileName}`;
        this.updateFileInput(componentId, message, color)
    }

    static updateFileInput(componentId, message, color) {
        let fileLabel = document.querySelector(`#${componentId}`);
        let rep = document.createElement("div");
        rep.setAttribute('class', 'styleFile');
        rep.setAttribute('id', componentId);
        rep.setAttribute('style', `color:${color};`);
        let text = document.createTextNode(message);
        rep.appendChild(text);
        fileLabel.parentNode.replaceChild(rep, fileLabel);
    }
}


class HttpRequestEngine {

    static fetchRequest(request) {
        return fetch(request)
            .then((res) => {
                if (res.ok) {
                    if (request.method === 'POST'){
                        UI.launchToastNotification('Success: Data was Saved');
                    }
                    return res.json();
                }
                throw new Error('Something went wrong');
            })
            .then((data) => data)
            .catch(error => {
                UI.launchToastNotification(error);
            });
    }

    static requestWithoutBody(uri, httpMethod) {

        let httpHeaders = new Headers();
        httpHeaders.append('Content-Type', 'application/json');
        httpHeaders.append('Authorisation', 'Bearer ');
        return  new Request(uri, {
            method: httpMethod,
            withCredentials: true,
            headers: httpHeaders
        });
    }


    static requestWithBody(uri, dataObj, httpMethod) {

        let httpHeaders = new Headers();
        httpHeaders.append('Content-Type', 'application/json');
        httpHeaders.append('Accept', 'application/json');
        return  new Request(uri, {
            method: httpMethod,
            withCredentials: true,
            headers: httpHeaders,
            body: JSON.stringify(dataObj)
        });
    }


    static fileUploadRequest(uri, dataObj, httpMethod) {

        let httpHeaders = new Headers();
        httpHeaders.append('Accept', 'application/json');
        return new Request(uri, {
            method: httpMethod,
            withCredentials: true,
            headers: httpHeaders,
            body: dataObj
        });
    }

}


class Validation {

    static validateInputs(validationObject) {
        for (const [id, description] of Object.entries(validationObject)) {
            let elem = document.getElementById(id);
            if (elem.value.replace(/^\s+|\s+$/g, '').length === 0) {
                let report = `Please ${description} must not be empty`;
                UI.launchToastNotification(report);
                elem.placeholder = report;
                elem.focus();
                return "invalid";
            }
        }
    }
}



