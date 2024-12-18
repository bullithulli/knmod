import matplotlib.pyplot as plt
import networkx as nx

# Create a directed graph
G = nx.DiGraph()

# Add nodes
G.add_nodes_from(["Start", "Process1", "Decision", "Process2", "End"])

# Add edges
G.add_edges_from([("Start", "Process1"), ("Process1", "Decision"), ("Decision", "Process2", {"label": "Yes"}),
                  ("Decision", "End", {"label": "No"})])

# Hierarchical layout assignment
pos = nx.nx_agraph.graphviz_layout(G, prog="dot")

# Draw the graph
nx.draw(G, pos, with_labels=True, node_size=700, node_color="skyblue", font_size=8, font_color="black",
        font_weight="bold", arrowsize=20)

# Add edge labels
edge_labels = nx.get_edge_attributes(G, 'label')
nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_color='red')

# Print node coordinates, height, and width
for node, (x, y) in pos.items():
    node_data = G.nodes[node]
    width, height = node_data.get('width', 0.1), node_data.get('height', 0.1)
    print(f"Node: {node}, Coordinates: ({x:.2f}, {y:.2f}), Width: {width:.2f}, Height: {height:.2f}")

# Save the plot as a PNG file
plt.savefig("flowchart.png", format="png", dpi=300)

# Display the plot
plt.show()
