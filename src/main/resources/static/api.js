const buttons = document.getElementsByTagName("button");

async function postPicture(button)
{
    const formData = new FormData();
    const fileField = document.querySelector('input[type="file"]');
    const imageURL = document.getElementById("imgURL").value;
    const imageFileRadio = document.getElementById("imageFileRadio");
    const imageURLRadio = document.getElementById("imageURLRadio");
    const detectionCheckBox = document.getElementById("detectionChbx");

    if(undefined !== fileField.files[0] || (undefined !== imageURL && imageURL.length > 0))
    {
        clearImageRow();
        appendLoadingToButton(button);

        if(imageFileRadio.checked){
            formData.append("multipartFile", fileField.files[0]);
        }

        if(imageURLRadio.checked){
            formData.append("imageURL", imageURL);
        }
        formData.append("imageJson", "{\"label\":\""
            + document.getElementsByName("label")[0]
                .value + "\", \"enableObjectDetection\":"+detectionCheckBox.checked +"}");

        const fetchPromise = fetch('http://localhost:8080/images', {
            method: 'POST',
            body: formData
        });
        fetchPromise.then((response) => response.json())
            .then((result) => {
                console.log('Success:', result);
                let objectString = parseObjectsToString(result.imageObjects);
                appendImageToImageRow(result.fileName, objectString);
                initToolTips();
                removeLoadingFromButton(button);
            })
            .catch((error) => {
                console.error('Error:', error);
                removeLoadingFromButton(button);
            });
    }
}

async function getAllImages(button)
{
    clearImageRow();
    appendLoadingToButton(button);

    const fetchPromise = fetch('http://localhost:8080/images', {
        method: 'get'
    });

    fetchPromise.then((response) => response.json())
        .then((result) => {
            console.log('Success:', result);
            result.forEach(image => {
                let objectString = parseObjectsToString(image.imageObjects);
                appendImageToImageRow(image.fileName, objectString);
            });
            initToolTips();
            removeLoadingFromButton(button);
        })
        .catch((error) => {
            console.error('Error:', error);
            removeLoadingFromButton(button);
        });
}

async function getByImageId(button)
{
    const imageIdNum = document.getElementById("imageIdInput").value;
    if(undefined !== imageIdNum && imageIdNum.length > 0 && imageIdNum > 0)
    {
        clearImageRow();
        appendLoadingToButton(button);
        const fetchPromise = fetch('http://localhost:8080/images/' + imageIdNum, {
            method: 'GET',
        });
        fetchPromise.then((response) => response.json())
            .then((result) => {
                console.log('Success:', result);
                if(result.id)
                {
                    let objectString = parseObjectsToString(result.imageObjects);
                    appendImageToImageRow(result.fileName, objectString);
                    initToolTips();
                }
                removeLoadingFromButton(button);
            })
            .catch((error) => {
                console.error('Error:', error);
                removeLoadingFromButton(button);
            });
    }
}

async function getByImageObjects(button)
{

    const objectString = document.getElementById("imageObjectInput").value;
    if(undefined !== objectString && objectString.length > 0){
        clearImageRow();
        appendLoadingToButton(button);
        objectString.replace(", ", ",");
            const fetchPromise = fetch('http://localhost:8080/images?objects=' + objectString, {
            method: 'get'
        });

        fetchPromise.then((response) => response.json())
            .then((result) => {
                console.log('Success:', result);
                result.forEach(image => {
                    let objectString = parseObjectsToString(image.imageObjects);
                    appendImageToImageRow(image.fileName, objectString);
                });
                initToolTips();
                removeLoadingFromButton(button);
            })
            .catch((error) => {
                console.error('Error:', error);
                removeLoadingFromButton(button);
            });
    }
}

function appendLoadingToButton(button)
{
    let spinnerSpan = document.createElement("span");
    spinnerSpan.classList.add("spinner-border","spinner-border-sm");
    spinnerSpan.setAttribute("role", "status");
    spinnerSpan.setAttribute("aria-hidden", "true");
    spinnerSpan.setAttribute("id", "spinnerId");

    button.prepend(spinnerSpan);
    disableAllButtons();

}

function removeLoadingFromButton(button){
    let spinnerSpan = document.getElementById("spinnerId");
    spinnerSpan.remove();
    enableAllButtons();
}

function disableAllButtons(){
    for (const button of buttons) {
        button.disabled = true;
    }
}

function enableAllButtons(){
    for (const button of buttons) {
        button.disabled = false;
    }
}

function clearImageRow()
{
    const imageContainers = document.getElementsByClassName("image-container");
    if(undefined != imageContainers)
    {
        Array.from(imageContainers).forEach(imageContainer => {
            imageContainer.remove();
        });
    }
}

function appendImageToImageRow(imageName, imageObjects)
{
    let imageContainer = document.createElement("div");
    imageContainer.classList.add("col", "image-container");
    let image = document.createElement("img");
    image.classList.add("img-fluid");
    image.setAttribute("data-bs-toggle", "tooltip");
    image.setAttribute("data-bs-placement", "top");
    image.setAttribute("data-bs-title", imageObjects);
    image.src = "imgs/" + imageName;
    imageContainer.appendChild(image);
    document.getElementById("imageRowId").appendChild(imageContainer);
}

function parseObjectsToString(imageObjects) {
    let imageObjectString = "No objects detected!"

    if(undefined != imageObjects || imageObjects.length > 0)
    {
        imageObjectString = "Detected objects: ";
        imageObjects.forEach(imageObject => {
            imageObjectString += imageObject.name + " ";
        });
    }
    imageObjectString.trimEnd();
    return imageObjectString;
}

function initToolTips()
{
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    })
}

function showField(button)
{
    if(button.id === "imageFileRadio")
    {
        document.getElementById("imageFileContainer").hidden = false;
        document.getElementById("imageURLContainer").hidden = true;
    }

    if(button.id === "imageURLRadio")
    {
        document.getElementById("imageFileContainer").hidden = true;
        document.getElementById("imageURLContainer").hidden = false;
    }
}
