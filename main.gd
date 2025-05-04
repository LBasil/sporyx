extends Node2D

var inventory_ui = null
var explore_ui = null
var arena_ui = null
@onready var ui_container = $UI

func _ready():
	print("Initializing UI...")
	
	if ResourceLoader.exists("res://Inventory.tscn"):
		var inventory_scene = load("res://Inventory.tscn")
		if inventory_scene:
			inventory_ui = inventory_scene.instantiate()
			ui_container.add_child(inventory_ui)
			inventory_ui.visible = false
			print("Inventory UI loaded")
		else:
			print("Error: Failed to load res://Inventory.tscn")
	else:
		print("Error: res://Inventory.tscn does not exist")
	
	if ResourceLoader.exists("res://Explore.tscn"):
		var explore_scene = load("res://Explore.tscn")
		if explore_scene:
			explore_ui = explore_scene.instantiate()
			ui_container.add_child(explore_ui)
			explore_ui.visible = false
			print("Explore UI loaded")
		else:
			print("Error: Failed to load res://Explore.tscn")
	else:
		print("Error: res://Explore.tscn does not exist")
	
	if ResourceLoader.exists("res://Arena.tscn"):
		var arena_scene = load("res://Arena.tscn")
		if arena_scene:
			arena_ui = arena_scene.instantiate()
			ui_container.add_child(arena_ui)
			arena_ui.visible = false
			arena_ui.fight_request.connect(_on_fight_request)
			print("Arena UI loaded")
		else:
			print("Error: Failed to load res://Arena.tscn")
	else:
		print("Error: res://Arena.tscn does not exist")
	
	$UI/InventoryButton.pressed.connect(_on_inventory_button_pressed)
	$UI/ExploreButton.pressed.connect(_on_explore_button_pressed)
	$UI/ArenaButton.pressed.connect(_on_arena_button_pressed)

func _on_inventory_button_pressed():
	if inventory_ui:
		inventory_ui.visible = !inventory_ui.visible
		if explore_ui: explore_ui.visible = false
		if arena_ui: arena_ui.visible = false

func _on_explore_button_pressed():
	if explore_ui:
		explore_ui.visible = !explore_ui.visible
		if inventory_ui: inventory_ui.visible = false
		if arena_ui: arena_ui.visible = false

func _on_arena_button_pressed():
	if arena_ui:
		arena_ui.visible = !arena_ui.visible
		if inventory_ui: inventory_ui.visible = false
		if explore_ui: explore_ui.visible = false
		send_ping_request()

func send_ping_request():
	var http_request = HTTPRequest.new()
	add_child(http_request)
	http_request.request_completed.connect(_on_request_completed)
	
	var error = http_request.request("http://127.0.0.1:8080/ping")
	if error != OK:
		print("Error sending request: ", error)
	else:
		print("Request sent successfully")

func _on_request_completed(result, response_code, headers, body):
	print("Request completed with result: ", result)
	print("Response code: ", response_code)
	print("Headers: ", headers)
	print("Body (raw): ", body)
	if response_code == 200:
		var response = body.get_string_from_utf8()
		print("Server response (parsed): ", response)
		if arena_ui and arena_ui.visible:
			arena_ui.update_combat_log(response)

func _on_fight_request(player_stats):
	var http_request = HTTPRequest.new()
	add_child(http_request)
	http_request.request_completed.connect(_on_fight_request_completed)
	
	var error = http_request.request("http://127.0.0.1:8080/fight", ["Content-Type: application/json"], HTTPClient.METHOD_POST, "{}")
	if error != OK:
		print("Error sending fight request: ", error)
	else:
		print("Fight request sent successfully")

func _on_fight_request_completed(result, response_code, headers, body):
	print("Fight request completed with result: ", result)
	print("Response code: ", response_code)
	print("Headers: ", headers)
	print("Body (raw): ", body)
	if response_code == 200:
		var response = body.get_string_from_utf8()
		print("Server response (parsed): ", response)
		if arena_ui and arena_ui.visible:
			arena_ui.update_combat_log(response)
