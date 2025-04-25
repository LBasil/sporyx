extends Node2D

var inventory_ui = null
var explore_ui = null
var arena_ui = null
@onready var ui_container = $UI

func _ready():
    if ResourceLoader.exists("res://Inventory.tscn"):
        inventory_ui = load("res://Inventory.tscn").instantiate()
        ui_container.add_child(inventory_ui)
        inventory_ui.visible = false
    if ResourceLoader.exists("res://Explore.tscn"):
        explore_ui = load("res://Explore.tscn").instantiate()
        ui_container.add_child(explore_ui)
        explore_ui.visible = false
    if ResourceLoader.exists("res://Arena.tscn"):
        arena_ui = load("res://Arena.tscn").instantiate()
        ui_container.add_child(arena_ui)
        arena_ui.visible = false
    
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