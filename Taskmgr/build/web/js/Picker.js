/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
  * Add support for Array.some() on older browsers.
  */
if (!Array.prototype.some)
{
 Array.prototype.some = function(fun)
 {
   var len = this.length;
   if (typeof fun != "function")
     throw new TypeError();

   var thisp = arguments[1];
   for (var i = 0; i < len; i++)
   {
     if (i in this &&
         fun.call(thisp, this[i], i, this))
       return true;
   }

   return false;
 };
}

var Picker = {
  AUTH_SCOPE: 'http://www.google.com/m8/feeds/',
  CONTACTS_URL: 'http://www.google.com/m8/feeds/contacts/default/full',
  GROUPS_URL: 'http://www.google.com/m8/feeds/groups/default/full',

  GROUPS_PANE_ID: 'picker_groups_pane',
  CONTACTS_PANE_ID: 'picker_contacts_pane',
  INFO_PANEL_ID: 'picker_info_pane',
  INFO_CONTAINER_ID: 'picker_info_container',

  GROUPS_LIST_ID: 'picker_groups',
  CONTACT_LIST_ID: 'picker_contacts',

  GROUP_SELECTED_CLASS: 'picker_selected',
  GROUP_END_SPECIAL_CLASS: 'picker_endspecial',
  CONTACT_SELECTED_CLASS: 'picker_selected',

  CONTACT_INFO_BLOCK_CLASS: 'picker_info_block',
  CONTACT_INFO_TITLE_CLASS: 'picker_info_title',
  CONTACT_INFO_META_CLASS: 'picker_info_meta',
  GOOGLE_MAPS_QUERY_URL: 'http://maps.google.com/?q=',

  serviceName: 0,
  contactsService: 0,
  container: 0,
  groupSelector: 0,
  userAddCallback: 0,
  userRemoveCallback: 0,
  errorCallback: 0,
  selectedGroup: 0,
  selectedUser: 0,
  selectedUsers: [],

  /* Callback functions */

  /**
   * Callback function invoked by the Google Contacts library after
   * calling populateGroups(). Generates the DOM entries that comprise the
   * list of group entries. Do not call directly.
   *
   * @param feedRoot The list of groups as returned by the Contacts
   *        service.
   */
  processGroupFeed: function(feedRoot) {
    // Convert result set to an array of entries
    var entries = feedRoot.feed.entry;

    var newGroupList = document.createElement('ul');
    newGroupList.id = Picker.GROUPS_LIST_ID;

    // Unconditionally add an 'All Contacts' group
    var allContactsGroup = document.createElement('li');
    allContactsGroup.id = 'picker-group-all';
    allContactsGroup.className = Picker.GROUP_END_SPECIAL_CLASS;
    var allContactsGroupSelector = document.createElement('a')
    allContactsGroupSelector.setAttribute('href',
        'javascript:Picker.displayContactGroup(\'' + Picker.CONTACTS_URL +
        '\', \'' + allContactsGroup.id + '\')');
    allContactsGroupSelector.appendChild(document.createTextNode(
        'All Contacts'));
    allContactsGroup.appendChild(allContactsGroupSelector);
    newGroupList.appendChild(allContactsGroup);

    // Write out new list of groups
    for (var i = 0; i < entries.length; i++) {
      var entry = entries[i];
      var newGroup = document.createElement('li');
      newGroup.id = 'picker-group-' + i;

      // Create a hyperlink to represent the group
      var newGroupSelector = document.createElement('a');
      newGroupSelector.setAttribute('href',
          'javascript:Picker.displayContactGroup(\''
          + entry.getId().getValue() + '\', \'' + newGroup.id + '\')');
      var name = entry.getTitle().getText();

      // Finalize group and add to groups pane
      newGroupSelector.appendChild(document.createTextNode(name));
      newGroup.appendChild(newGroupSelector);
      newGroupList.appendChild(newGroup);
    }

    // Replace the old group list with the newly generated one
    var groupsPane = document.getElementById(Picker.GROUPS_PANE_ID);
    var oldGroupsList = document.getElementById(Picker.GROUPS_LIST_ID);
    groupsPane.replaceChild(newGroupList, oldGroupsList);

    // Update the list of contacts
    Picker.displayContactGroup(Picker.CONTACTS_URL, allContactsGroup.id);
  },

  /**
   * Callback function invoked by the Google Contacts library after calling
   * populateContatcts(). Generates the DOM entries that comprise the list
   * of contact entries. Do not call directly.
   *
   * @param feedRoot The list of contact entries as returned by the
   *        Contacts service.
   */
  processContactFeed: function(feedRoot) {
    // Convert result set to an array of entries
    var entries = feedRoot.feed.entry;

    var newContactList = document.createElement('ul');
    newContactList.id = Picker.CONTACT_LIST_ID;
    // Write out new list of groups
    for (var i = 0; i < entries.length; i++) {
      var entry = entries[i];
      var id = entry.getId().getValue()
      var newContact = document.createElement('li');
      newContact.id = 'picker_contact_' + i;

      // Create a hyperlink to allow viewing contact details
      var newContactDetailsLink = document.createElement('a');
      newContactDetailsLink.setAttribute('href',
          'javascript:Picker.showContactDetails(\'' + id + '\', \'' +
          newContact.id + '\')')

      // Create a checkbox to allow selection of this contact
      var newContactSelector = document.createElement('input');
      newContactSelector.type = 'checkbox';
      newContactSelector.name = 'contact_selector';
      newContactSelector.id = 'contact_selector_' + i;
      newContactSelector.value = id;

      // Check to see if the contact has already been added before,
      // perhaps while browsing a differnt group
      var isExistingUser = Picker.selectedUsers.some(
          function(selectedUserId) {
            return (selectedUserId == newContactSelector.value)
          }
      );
      if (isExistingUser) {
        newContactSelector.checked = true;
      }

      // Now that we've optionally toggled the contact's checkbox, we
      // can set an event handler
      newContactSelector.onclick = function() {Picker.updateStatus(this.id);};

      // List the contact's name next to the checkbox
      var name = 0;
      if (entry.getTitle() && entry.getTitle().getText()) {
        name = entry.getTitle().getText();
      } else if (entry.getEmailAddresses()) {
        name = entry.getEmailAddresses()[0].getAddress();
      } else {
        // This should never actually be reached, since users are currently
        // required to have either a name or an email address
        name = 'Untitled Contact';
      }
      var newContactLabel = document.createTextNode(name);

      newContactDetailsLink.appendChild(newContactSelector);
      newContactDetailsLink.appendChild(newContactLabel);
      newContact.appendChild(newContactDetailsLink);
      newContactList.appendChild(newContact);
    }

    // Replace the old contact list with the newly generated one
    var contactsPane = document.getElementById(Picker.CONTACTS_PANE_ID);
    var oldContactList = document.getElementById(Picker.CONTACT_LIST_ID);
    contactsPane.replaceChild(newContactList, oldContactList);
  },

  /**
   * Display the details for a given conatct in the info pane.
   */
  processContactDetails: function(entryRoot) {
    var entry = entryRoot.entry;

    // Create a new div to hold the user's info
    var newPanel = document.createElement('div');
    newPanel.id = Picker.INFO_PANEL_ID;

    // Output the user's name and position
    var personalInfo = document.createElement('div');
    personalInfo.className = Picker.CONTACT_INFO_BLOCK_CLASS;

    var name = entry.getTitle();
    if (name && name.getText()) {
      var nameP = document.createElement('p');
      nameP.className = Picker.CONTACT_INFO_TITLE_CLASS;
      nameP.appendChild(document.createTextNode(name.getText()));
      personalInfo.appendChild(nameP);
    }

    var emails = entry.getEmailAddresses();
    for (var i = 0; i < emails.length; i++) {
      var email = emails[i];

      if (email.getAddress()) {
        var emailP = document.createElement('p');
        var emailA = document.createElement('a');
        var emailTxt = email.getAddress();
        emailA.setAttribute('href', 'mailto:' + emailTxt);
        emailA.appendChild(document.createTextNode(emailTxt)),
        emailP.appendChild(emailA);
        personalInfo.appendChild(emailP);
      }
    }

    var organizations = entry.getOrganizations();
    if (organizations[0]) {
      var title = organizations[0].getOrgTitle();
      if (title && title.getValue()) {
        var titleP = document.createElement('p');
        titleP.appendChild(document.createTextNode(title.getValue()));
        personalInfo.appendChild(titleP);
      }
      var organization = organizations[0].getOrgName();
      if (organization && organization.getValue()) {
        var organizationP = document.createElement('p');
        organizationP.appendChild(document.createTextNode(
            organization.getValue()));
        personalInfo.appendChild(organizationP);
      }
    }

    newPanel.appendChild(personalInfo);

    // List the contact's telephone information
    var telephoneInfo = document.createElement('div');
    telephoneInfo.className = Picker.CONTACT_INFO_BLOCK_CLASS;

    var phones = entry.getPhoneNumbers();
    for (var i = 0; i < phones.length; i++) {
      var phone = phones[i];

      if (phone.getValue()) {
        var phoneP = document.createElement('p');
        var phoneTxt = phone.getValue();
        phoneP.appendChild(document.createTextNode(phoneTxt + ' '));
        var phoneSpan = document.createElement('span');
        phoneSpan.className = Picker.CONTACT_INFO_META_CLASS;
        var phoneType = 0;
        if (phone.getLabel()) {
          phoneType = phone.getLabel();
        } else if (phone.getRel()) {
          phoneType = phone.getRel();
          // We're not provided with a human-readable label, so the
          // next line extracts the phone type directly from the fragment
          // name in the gd:phoneNumber's rel attribute.
          phoneType = phoneType.substring(33, phoneType.length);
        }
        if (phoneType) {
          phoneSpan.appendChild(document.createTextNode(phoneType));
          phoneP.appendChild(phoneSpan);
        }
        telephoneInfo.appendChild(phoneP);
      }
    }

    newPanel.appendChild(telephoneInfo);

    // List the contact's IM information
    var imInfo = document.createElement('div');
    imInfo.className = Picker.CONTACT_INFO_BLOCK_CLASS;

    var imAddresses = entry.getImAddresses();
    for (var i = 0; i < imAddresses.length; i++) {
      var imAddress = imAddresses[i];

      if (imAddress.getAddress()) {
        var imAddressP = document.createElement('p');
        var imAddressTxt = imAddress.getAddress();
        imAddressP.appendChild(document.createTextNode(imAddressTxt + ' '));
        var imAddressSpan = document.createElement('span');
        imAddressSpan.className = Picker.CONTACT_INFO_META_CLASS;
        if (imAddress.getProtocol()) {
          var imAddressProtocol = imAddress.getProtocol();
          // We're not provided with a human-friendly label, so the
          // next line extracts the protocol name directly from the fragment
          // name in the gd:imAddress's protocol attribute.
          imAddressProtocol = imAddressProtocol.substring(33,
              imAddressProtocol.length).toLowerCase();
          imAddressSpan.appendChild(
              document.createTextNode(imAddressProtocol));
          imAddressP.appendChild(imAddressSpan);
          imInfo.appendChild(imAddressP);
        }
      }
    }

    newPanel.appendChild(imInfo);

    // List the contact's postal address
    var postalInfo = document.createElement('div');
    postalInfo.className = Picker.CONTACT_INFO_BLOCK_CLASS;

    var postalAddresses = entry.getPostalAddresses();
    for (var i = 0; i < postalAddresses.length; i++) {
      var postalAddress = postalAddresses[i];

      if (postalAddress.getValue()) {
        var postalAddressP = document.createElement('p');
        var postalAddressTxt = postalAddress.getValue();
        // We're creating this element out of order because the postal
        // address will be transformed in a later step.
        var postalAddressMapHref = Picker.GOOGLE_MAPS_QUERY_URL
            + postalAddressTxt;
        while(postalAddressMapHref.match('\n'))
          postalAddressMapHref = postalAddressMapHref.replace('\n', ', ');
        var postalAddressMapLink = document.createElement('a');
        postalAddressMapLink.setAttribute('href', postalAddressMapHref);
        // Convert newlines in the postalAddressMapLink
        postalAddressMapLink.appendChild(document.createTextNode('map'));
        // Convert newlines in the postalAddressTxt to HTML equivilents
        // Keep in mind this logic should be more complicated. A
        // maliciously crafted address field could achieve a cross-site
        // scripting attack.
        while(postalAddressTxt.match('\n'))
          postalAddressTxt = postalAddressTxt.replace('\n', '<br />');
        postalAddressP.innerHTML = postalAddressTxt + '<br />';
        var postalAddressSpan = document.createElement('span');
        postalAddressSpan.className = Picker.CONTACT_INFO_META_CLASS;
        var postalAddressType = postalAddress.getRel();
        // We're not provided with a human-friendly label, so the
        // next line extracts the address type directly from the fragment
        // name in the gd:postalAddress's rel attribute.
        postalAddressType = postalAddressType.substring(33,
            postalAddressType.length).toLowerCase()
        postalAddressSpan.appendChild(document.createTextNode(
            postalAddressType));
        postalAddressP.appendChild(postalAddressSpan);
        postalAddressP.appendChild(document.createTextNode(' - '));
        postalAddressP.appendChild(postalAddressMapLink);
        postalInfo.appendChild(postalAddressP);
      }
    }

    newPanel.appendChild(postalInfo);

    // List any notes associated with the contact
    // List the contact's IM information
    var notesInfo = document.createElement('div');
    notesInfo.className = Picker.CONTACT_INFO_BLOCK_CLASS;

    var notes = entry.getContent();
    if (notes && notes.getText()) {
      var notesP = document.createElement('p');
      var notesTxt = notes.getText();
      notesP.appendChild(document.createTextNode(notesTxt));
      notesInfo.appendChild(notesP);
    }

    newPanel.appendChild(notesInfo);

    // Update the contacts view
    var infoContainer = document.getElementById(Picker.INFO_CONTAINER_ID)
    var oldPanel = document.getElementById(Picker.INFO_PANEL_ID);
    infoContainer.replaceChild(newPanel, oldPanel);
  },

  /**
   * Callback method invoked when a user has been added by selecting the
   * user's checkbox. Used by updateStatus(), do not call directly.
   *
   * @param entryRoot The root node for the entry that should be added
   *        as returned by the Contacts service.
   */
  callUserAddCallback: function(entryRoot) {
    var entry = entryRoot.entry;
    var id = entry.getId().getValue();

    // Check to see if the contact has already been added. If not, add
    // the contact to the list of added IDs.
    var match = Picker.selectedUsers.some(function(selectedUserId) {
      return (selectedUserId == id);
    });

    if (!match) {
      Picker.selectedUsers.push(id);
    } else {
      Picker.error('Attempt to add a contact which has already been added.');
    }

    // Invoke callback
    if (Picker.userAddCallback != 0 && typeof(Picker.userAddCallback) !=
        'undefined') {
      Picker.userAddCallback(entry);
    } else {
      Picker.error('User callback function undefined: userAddCallback');
    }
  },

  /**
   * Callback method invoked when a user has been removed by deselecting
   * the user's checkbox. Used by updateStatus(), do not call directly.
   *
   * @param entryRoot The root node for the entry that should be removed
   *        as returned by the Contacts service.
   */
  callUserRemoveCallback: function(entryRoot) {
    var entry = entryRoot.entry;
    var id = entry.getId().getValue();

    // Check to see if the contact has already been added. If so, remove
    // the contact to the list of added IDs.
    var match = false;
    for (var i = 0; i < Picker.selectedUsers.length; i++) {
      if (Picker.selectedUsers[i] == id) {
        match = true;
        break;
      }
    }

    if (match) {
      Picker.selectedUsers.splice(i, 1);
    } else {
      Picker.error('Attempt to remove a contact which has not been added.');
    }

    // Invoke callback
    if (Picker.userRemoveCallback != 0 && typeof(Picker.userRemoveCallback)
        != 'undefined') {
      Picker.userRemoveCallback(entry);
    } else {
      Picker.error('User callback function undefined: userRemoveCallback');
    }
  },

  /* Class methods */

  /**
   * Create an authenticated session with the Google Contacts server.
   * Requires that setServiceName() has been called previously.
   */
  login: function() {
    if (this.serviceName != 0 && typeof(this.serviceName) != 'undefined') {
      // Obtain a login token
      google.accounts.user.login(this.AUTH_SCOPE);
      // Create a new persistant service object
      this.contactsService = new google.gdata.contacts.ContactsService(
        this.serviceName);
    } else {
      this.error('Service name undefined, call setServiceName()');
    }
  },

  /**
   * Destroy the current Google Contacts session, including cached
   * authentication tokens.
   */
    logout: function() {
      google.accounts.user.logout();
      this.container.innerHTML = '<h3 align=\'center\'>Please Sign In To' +
        ' Use This Feature</h3><p align=\'center\'><input type=\'button\'' +
        ' value=\'Sign In\' onclick=\'Picker.login()\' /></p>';
    },

  /**
   * Display an error message. Custom behavior can be defined by using
   * setErrorCallback() to register a callback function.
   *
   * @param errorMessage The error string to display.
   */
  error: function(errorMessage) {
    if (this.errorCallback != 0 && typeof(this.errorCallback)
        != 'undefined') {
      this.errorCallback(errorMessage);
    } else {
      alert(errorMessage);
    }
  },

  /**
   * Set the service name which will be provided to the Google Contacts
   * servers during login. This name should uniquely identify yourself,
   * your application's name, and your application's version. For example:
   * "Google-PickerSample-1-0" would represent Google Picker Sample v1.0.
   *
   * @param serviceName A string indicating your service's name.
   */
  setServiceName: function(serviceName) {
    this.serviceName = serviceName;
  },

  /**
   * Set a callback function which will be invoked when a error is
   * thrown. If not set or set to 0, then alert() will be used as a
   * default error handler. This function should accept a single
   * parameter: A string representing the error message.
   *
   * @param callback The callback function to use, or 0 if alert() should
   *        be used instead.
   */
  setErrorCallback: function(callback) {
    this.errorCallback = callback;
  },

  /**
   * Set a callback function which will be invoked when a new user is
   * selected. This function should accept a single parameter: a
   * ContactEntry representing the newly selected contact.
   */
  setUserAddCallback: function(callback) {
    this.userAddCallback = callback;
  },

  /**
   * Set a callback function which will be invoked when a user is
   * deselected. This function should accept a single parameter: a
   * ContactEntry representing the newly deselected contact.
   */
  setUserRemoveCallback: function(callback) {
    this.userRemoveCallback = callback;
  },

  /**
   * Display details for a given contact.
   *
   * @param groupId The Atom ID of the group which should be retrieved.
   * @param elementId The DOM element ID which should be marked as
   *        selected.
   */
  showContactDetails: function(atomId, elementId) {
    // Retrieve details for contact
    this.contactsService.getContactEntry(
      atomId,
      this.processContactDetails,
      this.error);

    // De-select the old contact, if any
    if (this.selectedContact != 0 && typeof(this.selectedContact) !=
        'undefined') {
      this.selectedContact.className = '';
    }

    // Mark the new contact as selected
    var element = document.getElementById(elementId);
    element.className = this.CONTACT_SELECTED_CLASS;
    this.selectedContact = element;
  },

  /**
   * Update whether a given entry is selected or not. Automatically
   * invoked whenver a contact's checkbox is toggled.
   *
   * @param selectorId The ID of the DOM node whose status has been
   *        changed. Ordinarily the ID of the checkbox invoking this
   *        method.
   */
  updateStatus: function(selectorId) {
    // Get the ID of the contact selected, as well as the state of the
    // checkbox
    var selector = document.getElementById(selectorId);
    var contactID = selector.value;
    var contactState = selector.checked;

    // Retrieve user information and dispatch to user callback
    if (contactState == true) {
      // Adding new user
      this.contactsService.getContactEntry(
        contactID,
        this.callUserAddCallback,
        this.error);
    } else {
      // Removing existing user
      this.contactsService.getContactEntry(
        contactID,
        this.callUserRemoveCallback,
        this.error);
    }
  },

  /**
   * Retrieve the list of user's groups and populate the group list. */
  populateGroups: function() {
    // Compose a new query requesting all groups
    var query = new google.gdata.contacts.ContactQuery(this.GROUPS_URL);
    query.setParam('max-results', 1000);

    // Submit query for asynchronous execution. After execution,
    // processGroupFeed() will be called on success, error() on
    // failure.
    this.contactsService.getContactGroupFeed(
        query, this.processGroupFeed, this.error);
  },

  /**
   * Retrieve the group ID associated with a DOM group selector, mark it
   * as selected, then retrieve the list of contact entries for the current
   * group and populate the contact list.
   *
   * @param groupId The Atom ID of the group which should be retrieved.
   * @param elementId The DOM element ID which should be marked as
   *        selected.
   */
  displayContactGroup: function(groupId, elementId) {
    // Locate the given DOM element
    var selector = document.getElementById(elementId);

    // De-select the old group
    if (this.selectedGroup != 0 &&
          typeof(this.selectedGroup) != 'undefined') {
      if (this.selectedGroup.className ==
          this.GROUP_SELECTED_CLASS + ' ' + this.GROUP_END_SPECIAL_CLASS) {
        this.selectedGroup.className = this.GROUP_END_SPECIAL_CLASS;
      } else {
        this.selectedGroup.className = '';
      }
    }

    // Select the new group
    if (selector.className == this.GROUP_END_SPECIAL_CLASS
        || selector.className == this.GROUP_SELECTED_CLASS + ' '
        + this.GROUP_END_SPECIAL_CLASS) {
      selector.className =
         this.GROUP_SELECTED_CLASS + ' ' + this.GROUP_END_SPECIAL_CLASS;
    } else {
      selector.className = this.GROUP_SELECTED_CLASS;
    }
    this.selectedGroup = selector;

    // Compose a new query requesting all contacts for the given group
    var query = new google.gdata.contacts.ContactQuery(this.CONTACTS_URL);
    query.setParam('max-results', 1000);
    if (groupId != Picker.CONTACTS_URL)
      query.setParam('group', groupId);

    // Submit query for asynchronous execution. After execution,
    // processGroupFeed() will be called on success, error() on
    // failure.
    this.contactsService.getContactFeed(query, this.processContactFeed,
        this.error);
  },

  /**
   * Create the initial layout for this class and insert it into a div
   * with the given name.
   *
   * @param containerId The name of the div which will hold the contact
   *        picker.
   */
  render: function(containerId) {
    // Import the stylesheet for this script
    var stylesheet = document.createElement('style');
    stylesheet.setAttribute('type', 'text/css');
    var rulesDef = '\
      #' + containerId + ' {\
        font: 100% Arial, sans-serif;\
        background-color: #fff;\
        border: 2px solid #c3d9ff;\
        -moz-border-radius: 3px;\
        -webkit-border-radius: 3px;\
        position: relative;\
        top: 0.5em;\
      }\
      \
      #' + containerId + ' div {\
        height: 100%;\
        float: left;\
        margin: 0;\
        padding: 0;\
      }\
      \
      #' + containerId + ' div#picker_header_pane {\
        height: 32px;\
        background-color: #e0ecff;\
        vertical-align: middle;\
        width: 100%;\
      }\
      \
      #' + containerId + ' div#picker_groups_container {\
        width: 25%;\
        height: 250px;\
        position: relative;\
        border-right: 2px solid #c3d9ff;\
      }\
      \
      #' + containerId + ' div#picker_contacts_container {\
        width: 30%;\
        height: 250px;\
        position: relative;\
        border-right: 2px solid #c3d9ff;\
      }\
      \
      #' + containerId + ' div#picker_info_container {\
        width: 44%;\
        height: 250px;\
        position: relative;\
      }\
      \
      #' + containerId + ' div.picker_column {\
        width: 100%;\
      }\
      \
      #' + containerId + ' div#picker_groups_pane {\
        position: static;\
        overflow: auto;\
      }\
      \
      #' + containerId + ' div#picker_groups_pane li {\
        overflow: hidden;\
      }\
      \
      #' + containerId + ' div#picker_groups_pane li a {\
        width: 9999%;\
      }\
      \
      #' + containerId + ' div#picker_contacts_pane {\
        position: static;\
        overflow: auto;\
      }\
      \
      #' + containerId + ' div#picker_contacts_pane li {\
        overflow: hidden;\
      }\
      \
      #' + containerId + ' div#picker_contacts_pane li a {\
        width: 9999%;\
      }\
      \
      #' + containerId + ' div#picker_info_pane {\
        width: 100%;\
        position: relative;\
        overflow: auto;\
      }\
      \
      #' + containerId + ' div#picker_footer_pane {\
        height: 32px;\
        background-color: #e0ecff;\
        vertical-align: middle;\
        width: 100%;\
        clear: both;\
        float: none;\
      }\
      \
      #' + containerId + ' #picker_title {\
        font-weight: bold;\
        margin: 0;\
        padding: 0.4em;\
        height: 100%;\
        vertical-align: middle;\
        font-size: 125%;\
      }\
      \
      #' + containerId + ' ul {\
        list-style-type: none;\
        padding: 0;\
        margin: 0;\
      }\
      \
      #' + containerId + ' li {\
        margin: 0;\
        padding: 2px 5px;\
        height: 1.3em;\
        line-height: 1.3em;\
      }\
      \
      #' + containerId + ' li input {\
        margin: 0 3px;\
      }\
      \
      #' + containerId + ' li.picker_endspecial {\
        border-bottom: 1px solid #c3d9ff;\
      }\
      \
      #' + containerId + ' li a {\
        display: block;\
        color: #000;\
        text-decoration: none;\
      }\
      \
      #' + containerId + ' li.picker_selected, #' + containerId
          + ' li.picker_selected:hover {\
        background-color: #c3d9ff;\
        color: #0000cc;\
        font-weight: bold;\
      }\
      \
      #' + containerId + ' li.picker_selected a, #' + containerId
          + ' li.picker_selected:hover a {\
        color: #0000cc;\
      }\
      \
      #' + containerId + ' li:hover {\
        background-color:  #ffffcc;\
      }\
      \
      #' + containerId + ' .' + Picker.CONTACT_INFO_BLOCK_CLASS + ' {\
        float: none;\
        width: 95%;\
        position: static;\
        padding: 0.5em;\
        height: auto;\
      }\
      \
      #' + containerId + ' #picker_info_pane p {\
        margin: 0;\
        padding: 0;\
      }\
      \
      #' + containerId + ' .' + Picker.CONTACT_INFO_TITLE_CLASS + ' {\
        font-weight: bold;\
        font-size: 1.1em;\
      }\
      \
      #' + containerId + ' .' + Picker.CONTACT_INFO_META_CLASS + ' {\
        color: #777;\
      }\
      \
      #' + containerId + ' #picker_logout {\
        padding: 0.6em;\
        margin: 0 1em;\
        line-height: 32px;\
      }\
      \
      #' + containerId + ' #picker_logout a {\
        text-decoration: none;\
      }';
    if (stylesheet.styleSheet) {
      // IE-specific hack
      stylesheet.styleSheet.cssText = rulesDef;
    } else {
      // Everything else
      var rulesNode = document.createTextNode(rulesDef);
      stylesheet.appendChild(rulesNode);
    }
    document.getElementsByTagName('head')[0].appendChild(stylesheet);

    this.container = document.getElementById(containerId);

    // Make sure that the client library is initialized
    google.gdata.client.init(this.error);

    // Execute only if the current session is valid.
    if (google.accounts.user.checkLogin(this.AUTH_SCOPE)) {
      // Even though we're supposedly authenticated, let's be certain...
      this.login();

      // Render the basic UI framework
      this.container.innerHTML = '\
        <div id=\'picker_header_pane\'>\
          <p id=\'picker_title\'>Friend Picker Sample for Google Contacts\
              Data API</p>\
        </div>\
        <div id=\'picker_groups_container\'>\
        <div id=\'picker_groups_pane\' class=\'picker_column\'>\
             <ul id=\'picker_groups\'></ul>\
        </div>\
        </div>\
        <div id=\'picker_contacts_container\'>\
        <div id=\'picker_contacts_pane\' class=\'picker_column\'>\
          <ul id=\'picker_contacts\'></ul>\
        </div>\
        </div>\
        <div id=\'picker_info_container\'>\
          <div id=\'picker_info_pane\' class=\'picker_column\'></div>\
        </div>\
        <div id=\'picker_footer_pane\'><p align=\'right\' \
            id=\'picker_logout\'><a href=\'javascript:Picker.logout()\'>\
            &raquo; Logout</a></p>\
        </div>';

      // Begin loading data
      var groups = this.populateGroups();
    } else {
      // Display a login button
      this.container.innerHTML =
        '<h3 align=\'center\'>Please Sign In To Use This Feature</h3>\
          <p align=\'center\'><input type=\'button\' value=\'Sign In\'\
          onclick=\'Picker.login()\' /></p>';
    }
  }
}


