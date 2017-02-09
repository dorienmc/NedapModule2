/* initialise variables */

var input = document.querySelector('.new-blockitem input');

var blockitemContainer = document.querySelector('.blockitem-container');

var clearBtn = document.querySelector('.clear');
var addBtn = document.querySelector('.add');

/*  add event listeners to buttons */

addBtn.addEventListener('click', addBlockItem);
clearBtn.addEventListener('click', clearAll);

/* generic error handler */
function onError(error) {
  console.log(error);
}

/* display previously-saved stored blocked items on startup */

initialize();

function initialize() {
  var gettingAllStorageItems = browser.storage.local.get(null);
  gettingAllStorageItems.then((results) => {
    var blockKeys = Object.keys(results);
    for(blockKey of blockKeys) {
      displayBlockItem(blockKey);
    }
  }, onError);
}

/* Add a blockitem to the display, and storage */

function addBlockItem() {
  var blockItem = input.value;
  var gettingItem = browser.storage.local.get(blockItem);
  gettingItem.then((result) => {
    var objTest = Object.keys(result);
    if(objTest.length < 1 && blockItem !== '') {
      input.value = '';
      storeBlockItem(blockItem);
    }
  }, onError);
}

/* function to store a new blockitem in storage */

function storeBlockItem(input) {
  var storingBlockItem = browser.storage.local.set({ [input] : "" });
  storingBlockItem.then(() => {
    displayBlockItem(input);
  }, onError);
}

/* function to display a blockitem in the blockitem box */

function displayBlockItem(input) {

  /* create blockitem display box */
  var blockitem = document.createElement('div');
  var blockitemDisplay = document.createElement('div');
  var blockitemPara = document.createElement('p');
  var deleteBtn = document.createElement('button');
  var clearFix = document.createElement('div');

  blockitem.setAttribute('class','blockitem');

  blockitemPara.textContent = input;
  deleteBtn.setAttribute('class','delete');
  deleteBtn.textContent = 'Delete blockitem';
  clearFix.setAttribute('class','clearfix');

  blockitemDisplay.appendChild(blockitemPara);
  blockitemDisplay.appendChild(deleteBtn);
  blockitemDisplay.appendChild(clearFix);

  blockitem.appendChild(blockitemDisplay);

  /* set up listener for the delete functionality */

  deleteBtn.addEventListener('click',function(e){
    evtTgt = e.target;
    evtTgt.parentNode.parentNode.parentNode.removeChild(evtTgt.parentNode.parentNode);
    browser.storage.local.remove(input);
  })

  /* create blockitem edit box */
  var blockitemEdit = document.createElement('div');
  var blockitemBodyEdit = document.createElement('input');
  var clearFix2 = document.createElement('div');

  var updateBtn = document.createElement('button');
  var cancelBtn = document.createElement('button');

  updateBtn.setAttribute('class','update');
  updateBtn.textContent = 'Update blockitem';
  cancelBtn.setAttribute('class','cancel');
  cancelBtn.textContent = 'Cancel update';

  blockitemEdit.appendChild(blockitemBodyEdit);
  blockitemBodyEdit.textContent = input;
  blockitemEdit.appendChild(updateBtn);
  blockitemEdit.appendChild(cancelBtn);

  blockitemEdit.appendChild(clearFix2);
  clearFix2.setAttribute('class','clearfix');

  blockitem.appendChild(blockitemEdit);

  blockitemContainer.appendChild(blockitem);
  blockitemEdit.style.display = 'none';

  /* set up listeners for the update functionality */
  blockitemPara.addEventListener('click',function(){
    blockitemDisplay.style.display = 'none';
    blockitemEdit.style.display = 'block';
  })

  cancelBtn.addEventListener('click',function(){
    blockitemDisplay.style.display = 'block';
    blockitemEdit.style.display = 'none';
    blockitemBodyEdit.value = input;
  })

  updateBtn.addEventListener('click',function(){
    if(blockitemBodyEdit.value !== input) {
      updateBlockItem(title,blockitemBodyEdit.value);
      blockitem.parentNode.removeChild(blockitem);
    }
  });
}


/* function to update blockitems */

function updateBlockItem(delBlockItem,newInput) {
  var storingBlockItem = browser.storage.local.set({ [newInput] : "" });
  storingBlockItem.then(() => {
    if(delBlockItem !== newInput) {
      var removingBlockItem = browser.storage.local.remove(delBlockItem);
      removingBlockItem.then(() => {
        displayBlockItem(newInput);
      }, onError);
    } else {
      displayBlockItem(newInput);
    }
  }, onError);
}

/* Clear all blockitems from the display/storage */

function clearAll() {
  while (blockitemContainer.firstChild) {
      blockitemContainer.removeChild(blockitemContainer.firstChild);
  }
  browser.storage.local.clear();
}
