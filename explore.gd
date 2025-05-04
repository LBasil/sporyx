extends Control

var timer = 0.0
var timer_active = false
var available_worlds = []

func _ready():
    fetch_worlds_list()
    $Panel/VBoxContainer/ExploreButton.pressed.connect(_on_explore_button_pressed)

func _process(delta):
    if timer_active:
        timer -= delta
        update_combat_log("Exploration in progress... Time remaining: " + str(int(timer)) + "s")

func fetch_worlds_list():
    var http_request = HTTPRequest.new()
    add_child(http_request)
    http_request.request_completed.connect(_on_worlds_request_completed)
    
    var error = http_request.request("http://127.0.0.1:8080/worlds")
    if error != OK:
        update_combat_log("Error fetching worlds list: " + str(error))

func _on_worlds_request_completed(result, response_code, headers, body):
    if response_code == 200:
        var response = body.get_string_from_utf8()
        print("Worlds response: ", response) # Debugging
        available_worlds = []
        for line in response.split("\n"):
            if line != "Available worlds:" and line.strip_edges() != "":
                available_worlds.append(line.strip_edges())
                $Panel/VBoxContainer/WorldsList.add_item(line.strip_edges())
        update_combat_log("Worlds loaded successfully!")
    else:
        update_combat_log("Error fetching worlds: " + str(response_code))

func _on_explore_button_pressed():
    var selected = $Panel/VBoxContainer/WorldsList.get_selected_items()
    if selected.size() > 0:
        var world_index = selected[0]
        if world_index < available_worlds.size():
            var world_name = available_worlds[world_index]
            $Panel/VBoxContainer/WorldsList.deselect_all()
            start_exploration(world_name)

func start_exploration(world_name):
    var http_request = HTTPRequest.new()
    add_child(http_request)
    http_request.request_completed.connect(_on_explore_request_completed)
    
    var body = JSON.stringify({"world": world_name})
    var error = http_request.request("http://127.0.0.1:8080/explore", ["Content-Type: application/json"], HTTPClient.METHOD_POST, body)
    if error != OK:
        update_combat_log("Error sending exploration request: " + str(error))
    else:
        update_combat_log("Exploration request sent for " + world_name)
        timer = 10.0
        timer_active = true

func _on_explore_request_completed(result, response_code, headers, body):
    timer_active = false
    if response_code == 200:
        var response = body.get_string_from_utf8()
        print("Raw response: ", response) # Debugging
        update_combat_log(response)
    else:
        update_combat_log("Server error: " + str(response_code))

func update_combat_log(message):
    var lines = message.split("\n")
    var combat_log_lines = []
    for line in lines:
        if line.strip_edges() != "":
            combat_log_lines.append(line.strip_edges())
    if combat_log_lines.size() > 100:
        combat_log_lines = combat_log_lines.slice(combat_log_lines.size() - 100)
    $Panel/VBoxContainer/CombatLog.text = "[center]" + "\n".join(combat_log_lines) + "[/center]"
    $Panel/VBoxContainer/CombatLog.scroll_to_line($Panel/VBoxContainer/CombatLog.get_line_count() - 1)