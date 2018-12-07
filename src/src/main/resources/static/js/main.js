
$(function(){
    $('configForm').submit(function (evt) {
        evt.preventDefault();
    });

    $('#triggerType').change(function(evt) {
        var selectedType = $('#triggerType').find('option:selected').val();
        var goodSet = $('#goodSampleSetSelect');
        var goodLabel = $("label[for='goodSampleSetSelect']");
        var badSet = $("#badSampleSetSelect");
        var badLabel = $("label[for='badSampleSetSelect']");
        if(selectedType===3) {
            goodSet.removeAttr("hidden");
            goodLabel.removeAttr("hidden");
            badSet.attr("hidden", "");
            badLabel.attr("hidden", "");
        }else if (selectedType===4) {
            goodSet.attr("hidden", "");
            goodLabel.attr("hidden", "");
            badSet.attr("hidden", "");
            badLabel.attr("hidden", "");
        } else {
            goodLabel.removeAttr("hidden");
            goodSet.removeAttr("hidden");
            badSet.removeAttr("hidden");
            badLabel.removeAttr("hidden");
        }
    })
});

function refresh() {
    refreshRules();
    refreshActions();
    refreshSubs();
    refreshTriggers();
    refreshSampleSets();
}


function refreshRules(){
    getJson(
        "/rules",
        function(rules) {
            console.log("rules:", rules);
            var rulesList = $('#rulesList');
            rulesList.empty();

            rules.forEach(
                function (rule) {
                    rulesList.append(
                        "<li class='rule'>"
                        + "<code>"+rule.name+"</code>"
                        + "&nbsp<button aria-label='delete' onclick='deleteRule("+rule.id+")'>✖️</button>"
                        + "</li>"
                    )
                });
        })
}


function refreshActions(){
    getJson(
        "/actions",
        function(actions) {
            console.log(actions);
            var actionsList = $('#actionsList');
            var actionSelect = $('#actionForm');
            actionsList.empty();
            actionSelect.empty();''

            actions.forEach(
                function (action) {
                    actionsList.append(
                        "<li class='action'>"
                        + "<code>"+action.name+"</code>"
                        + "&nbsp<button aria-label='delete' onclick='deleteAction("+action.id+")'>✖️</button>"
                        + "</li>"
                    );

                    actionSelect.append(
                        "<option class='actionOption' value='" + action.id + "'>"
                        + "<code>"+action.name+"</code>"
                        + "</option>"
                    );
                }
            );
        }
    );
};


function refreshTriggers(){
    getJson(
        "/triggers",
        function(triggers){
            console.log(triggers);
            var triggersList = $('#triggersList');
            var triggerSelect = $('#triggerForm');

            triggersList.empty();
            triggerSelect.empty();

            triggers.forEach(
                function (trigger) {
                    triggersList.append(
                        "<li class='trigger'>"
                        + "<code>"+trigger.name+"</code>"
                        + "&nbsp<button aria-label='delete' onclick='deleteTrigger("+trigger.id+")'>✖️</button>"
                        + "</li>"
                    );

                    triggerSelect.append(
                        "<option class='triggerOption' value='"+trigger.id+"'>"
                        + "<code>"+trigger.name+"</code>"
                        + ""
                        + "</option>"
                    );
                }
            );
        }
    );
};


function refreshSubs(){
    getJson(
        "/subreddits",
        function(subs){
            console.log(subs);
            var subredditsList = $('#subredditsList');
            var subredditSelect = $('#ruleSubForm');
            subredditsList.empty();
            subredditSelect.empty();

            subs.forEach(
                function (sub) {
                    subredditsList.append(
                        "<li class='sub'>"
                        + "<code>"+sub.sub+"</code>"
                        + "&nbsp<button aria-label='delete' onclick='deleteSubreddit(\""+sub.sub+"\")'>✖️</button>"
                        + "</li>"
                    );

                    subredditSelect.append(
                        "<option class='subOption' value='"+sub.sub+"'>"
                        + "<code>"+sub.sub+"</code>"
                        + "</option>"
                    );
                }
            );
        }
    );
};


function refreshSampleSets() {
    getJson(
        "/commentSampleSets",
        function(sampleSets){
            console.log(sampleSets);
            var sampleSetList = $('#sampleSetList');
            var goodSampleSetSelect = $('#goodSampleSetSelect');
            var badSampleSetSelect = $('#badSampleSetSelect');
            sampleSetList.empty();
            goodSampleSetSelect.empty();
            badSampleSetSelect.empty();

            sampleSets.forEach(
                function (set) {
                    sampleSetList.append(
                        "<li class='sampleSet' data-done='"+set.done+"'>"
                        + "<code>"+set.name+"</code>"
                        + "&nbsp<button aria-label='delete' onclick='deleteSampleSet("+set.id+")'>✖️</button>"
                        + "</li>"
                    );

                    goodSampleSetSelect.append(
                        "<option class='setOption' value='"+set.id+"'>"
                        + "<code>"+set.name+"</code>"
                        + ""
                        + "</option>"
                    );

                    badSampleSetSelect.append(
                        "<option class='setOption' value='"+set.id+"'>"
                        + "<code>"+set.name+"</code>"
                        + ""
                        + "</option>"
                    );
                }
            );
        }
    );
}


function postJson(url, payload){
    var request = new XMLHttpRequest();
    request.open("POST", url, true);

    request.onreadystatechange = function() {
        if (this.readyState === 4 && this.status >= 200 && this.status <300) {
            window.location.replace(window.location.pathname + window.location.search + window.location.hash);
            window.location.reload(false);
        }
    };

    request.setRequestHeader("Content-type", "application/json");
    request.send(JSON.stringify(payload));
}


function getJson(url, callback) {
    var request = new XMLHttpRequest();

    request.onreadystatechange = function() {
        if (this.readyState === 4 && this.status <= 200 && this.status <300) {
            var items = JSON.parse(this.responseText);
            callback(items);
        }
    };

    request.open("GET", url, true);
    request.send();
}

function deleteJson(url, payload){
    var request = new XMLHttpRequest();
    request.open("delete", url, true);

    request.onreadystatechange = function() {
        if (this.readyState === 4 && this.status >= 200 && this.status <300) {
            window.location.replace(window.location.pathname + window.location.search + window.location.hash);
            window.location.reload(false);
        }
    };

    request.setRequestHeader("Content-type", "application/json");
    request.send(JSON.stringify(payload));
}

function submitSampleSetForm(){
    var link = $("#sampleLink").val();
    var name = $("#sampleName").val();
    var count = $("#sampleSize").val();

    var payload = {
        'name': name,
        'url' : link,
        'count': count
    };

    console.log(payload);
    postJson("/commentSampleSets", payload);
    return false;
}


function deleteSampleSet(id){
    deleteJson("/commentSampleSets", {id: id});
}



function submitSubredditsForm(){
    var subLink = $('#subLink').val();

    var payload = {
        subreddit: subLink
    };

    console.log(payload);
    postJson("/subreddits", payload);
    return false;
}


function deleteSubreddit(subreddit){
    deleteJson("/subreddits", {subreddit: subreddit});
    return false;
}


function submitRegexTriggerForm() {
    var regex = $('#regexForm').val();
    var name = $('#regexName').val();

    var payload = {
        type: 0,
        name: name,
        regex: regex,
        sampleSet: undefined
    };

    console.log(payload);
    postJson("/triggers", payload);
    return false;
}


function deleteTrigger(id){
    deleteJson("/triggers", {id: id});
    return false;
}


function submitTriggerForm() {
    var name = $('#triggerName').val();
    var type = $('#triggerType').find('option:selected').val();
    var goodSampleSet = $('#goodSampleSetSelect').find('option:selected').val();
    var badSampleSet = $('#badSampleSetSelect').find('option:selected').val();


    var payload = {
        name:name,
        regex:undefined,
        type:type,
        goodSampleSet:goodSampleSet,
        badSampleSet: badSampleSet
    };

    console.log(payload);
    postJson("/triggers", payload);
    return false;
}


function submitActionsMessageForm() {
    var name = $('#messageName').val();
    var type = 3;
    var message = $('#actionMessage').val();

    var payload = {
        name: name,
        type: type,
        message: message
    };

    console.log(payload);
    postJson("/actions", payload);
    return false;
}


function deleteAction(id){
    deleteJson("/actions", {id: id});
    return false;
}


function submitActionsForm() {
    var name = $('#actionName').val();
    var type = Number($('#actionType').find('option:selected').val());

    console.log(name);

    var payload = {
        name: name,
        type: type,
        message: undefined
    };

    console.log(payload);
    postJson("/actions", payload);
    return false;
}


function submitRuleForm() {
    var name = $('#ruleName').val();
    var sub = $('#ruleSubForm').find('option:selected').val();
    var trigger = $('#triggerForm').find('option:selected').val();
    var action = $('#actionForm').find('option:selected').val();

    var payload = {
        sub:sub,
        name:name,
        triggerId:trigger,
        actionId: action
    };

    console.log(payload);
    postJson("/rules", payload);
    return false;
}

function deleteRule(id){
    deleteJson("/rules", {id: id});
    return false;
}

