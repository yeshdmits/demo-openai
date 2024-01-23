function addRow() {
  const formContainer = document.getElementById('formContainer');
  const newRow = document.createElement('div');
  newRow.className = 'input-group flex-nowrap';

  const keyInput = document.createElement('input');
  keyInput.name = 'key';
  keyInput.type = 'text';
  keyInput.placeholder = 'Enter Key';
  keyInput.className = 'form-control'
  const labelInput = document.createElement('span');
  labelInput.className = 'input-group-text'
  labelInput.id = 'addon-wrapping'
  labelInput.textContent = '-'
  labelInput.onclick = function () {
    formContainer.removeChild(newRow);
  };

  newRow.appendChild(labelInput);
  newRow.appendChild(keyInput);

  formContainer.appendChild(newRow);
}

function onSubmitDynamic(event) {
  event.preventDefault();

  const form = document.getElementById('dynamicForm');
  const formData = new FormData(form);

  console.log(formData);
  // Convert FormData to an object
  const json = {};
  formData.forEach((value, key) => {
    json[value] = ""
  });

  console.log(json);
  document.querySelector('#request').innerHTML = JSON.stringify(json)
  sendRequest(JSON.stringify(json))
}

function onSubmitBulk(event) {
  event.preventDefault();

  const form = document.getElementById('bulkForm');
  const formData = new FormData(form);
  let json = formData.get("json");
  console.log(json);
  document.querySelector('#request').innerHTML = JSON.stringify(json)
  sendRequest(JSON.stringify(json))
}

function sendRequest(request) {
  let docId = document.querySelector('#session').innerHTML;
  const formData = new FormData();
  formData.append("request", request);

  fetch('/api/v1/document/' + docId, {
    method: 'POST',
    body: formData,
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    document.querySelector('#response').innerHTML = data.response;
    document.querySelector('#recognizedText').innerHTML = data.text;
  })
  .catch(error => {
    console.error('There was a problem with the fetch operation:', error);
    document.querySelector('#response').innerHTML = 'error';
  });
}


function uploadFile() {
  const form = document.getElementById('uploadFileForm');
  const formData = new FormData(form);
  let blob = formData.get("file");
  upload(blob)
}
const basicAuthToken = btoa(`admin:demo-openai`);
function upload(blob) {
  const oMyForm = new FormData();
  oMyForm.append("file", blob);

  fetch('/api/v1/upload', {
    method: 'POST',
    body: oMyForm,
    headers: {
      'Authorization': `Basic ${basicAuthToken}`,
      'Content-Type': 'multipart/form-data',
    },
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    document.querySelector('#sessionContainer').style.display = 'block';
    document.querySelector('#showPrompt').removeAttribute('disabled');
    document.querySelector('#session').innerHTML = data.uuid;
    document.querySelector('#recognizedText').innerHTML = data.text;
    document.querySelector('#requestOpenAI').removeAttribute('hidden');
  })
  .catch(error => {
    console.error('There was a problem with the fetch operation:', error);
    document.querySelector('#session').innerHTML = 'error';
    document.querySelector('#sessionContainer').style.display = 'block';
  });
}

function toggleSpeechForm() {
  const enableSpeechCheckbox = document.getElementById('enableSpeech');
  const speechFormContainer = document.getElementById('speechFormContainer');
  const speechFormContainerRecord = document.getElementById('speechFormContainerRecord');

  if (enableSpeechCheckbox.checked) {
    speechFormContainer.style.display = 'none';
    speechFormContainerRecord.style.display = 'block'
  } else {
    speechFormContainer.style.display = 'block';
    speechFormContainerRecord.style.display = 'none'
  }
}

function toggleDynamicBulkRequest() {
  const enableSpeechCheckbox = document.getElementById('dynamicBulkRequest');
  const dynamicFormContainer = document.getElementById('dynamicFormContainer');
  const bulkFormContainer = document.getElementById('bulkFormContainer');

  if (enableSpeechCheckbox.checked) {
    dynamicFormContainer.style.display = 'none';
    bulkFormContainer.style.display = 'block'
  } else {
    dynamicFormContainer.style.display = 'block';
    bulkFormContainer.style.display = 'none'
  }
}

function resizeBulk() {
  let bulkFormJson = document.getElementById('bulkFormJson');
  bulkFormJson.style.height = 'auto'
  bulkFormJson.style.height=bulkFormJson.scrollHeight+'px';
}

function collapseText() {
  let collapseOne = document.getElementById('collapseOne');
  if (collapseOne.className === 'accordion-collapse') {
    collapseOne.className = 'accordion-collapse collapse'
  } else {
    collapseOne.className = 'accordion-collapse'
  }
}

