var doc = require('dynamodb-doc');
var AWS = require("aws-sdk");
var dynamodb = new doc.DynamoDB();
var events;
var curr_event;

exports.lambda_handler = function(event, context) {
    console.log("Request received:\n" );
    console.log(event)
    console.log("Context received:\n");
    console.log(context)
    events = event;
    handle_events();
};

var handle_events = function()
{
    curr_event = events.Records.shift();
    sorted_similar_users = [];
    similar_users = [];
    matched_users = [];
    user = 0;
    var data;
    console.log(curr_event);
    if( curr_event.eventName === "INSERT")
    {
        data = curr_event.dynamodb;
        item = {
            "userId": data.Keys.userId['S'],
            "coffeeCount": 0,
            "interests": [],
            "matched" : data.NewImage.matched['S']
        };
        data.NewImage.interests['L'].forEach(function(interest)
        {
            item.interests.push(interest['S']);
            console.log("Interest item:", interest['S']);
        });
        user = item.userId;
        console.log("Insert:", JSON.stringify(item));
        console.log("Adding to matching pool" + item.userId);
        add_to_matching_pool(item);
    }
    else if( curr_event.eventName === "MODIFY")
    {
        data = curr_event.dynamodb;
        console.log("Event right now\n",data);
        item = {
            "userId": data.Keys.userId['S'],
            "coffeeCount": data.NewImage.coffeeCount,
            "interests": [],
            "matched" : data.NewImage.matched['S']
        };
        data.NewImage.interests['L'].forEach(function(interest)
        {
            item.interests.push(interest['S']);
            console.log("Interest item:", interest['S']);
        });
        user = item.userId;
        if( item.matched === 'null')
        {
            console.log("Updating matching pool with " + item.userId);
            update_matching_pool(item);
        }

        console.log("Modify:", JSON.stringify(item));
        console.log("Interests list:", item.interests);
    }
    else
    {
        console.log("Remove_item");
    }

    if( events.Records.length > 0 ) {
       console.log("Next event")
       handle_events();
    }
}

var update_matching_pool = function(item)
{
    dynamodb.updateItem({
        "TableName" : "matchingPool",
        "Key" : {
            "userId" : item.userId
        },
        "UpdateExpression" : "SET #attrName = :attrValue",
        "ExpressionAttributeNames" : {
           "#attrName" : "interests"
        },
        "ExpressionAttributeValues" : {
            ":attrValue" : item.interests
        }
    }, function(err,data){
        if(err)
        {
            console.log('ERROR: Unable to update matching pool '+ item.userId +" " + err);
        }
        else
        {
           console.log('Updated matching pool '+ item.userId);
        }
    });
}

var add_to_matching_pool = function(item)
{
    sorted_similar_users = [];
    similar_users = [];
    matched_users = [];
    console.log("Matching pool function");

        var userItem = {
            "userId" : item.userId,
            "interests": item.interests
        };
    dynamodb.putItem({
        "TableName": "matchingPool",
        "Item": userItem
    }, function(err, data) {
        if (err)
        {
            console.log('ERROR: Unable to add to matching pool '+ item.userId +" " + err);
        }
        else
        {
          console.log("item added to pool" + item.userId);
        }
    });
}
