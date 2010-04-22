
var contactsService =
    new google.gdata.contacts.ContactsService('GoogleInc-jsguide-1.0');

// The feed URI that is used for retrieving group contacts
var feedUri = 'http://www.google.com/m8/feeds/groups/default/full';

// callback method to be invoked when getContactGroupFeed() returns data
var callback = function(result) {

  // An array of contact group entries
  var entries = result.feed.entry;

  // Iterate through the array of contact groups, and print out their title and ID
  for (var i = 0; i < entries.length; i++) {
    var groupEntry = entries[i];
    var groupTitle = groupEntry.getTitle().getText();
    var groupId = groupEntry.getId().getValue();

    alert('group title = ' + groupTitle);
    PRINT('group id = ' + groupId);
  }
}

// Error handler
var handleError = function(error) {
  alert(error);
}

// Submit the request using the contacts service object

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


