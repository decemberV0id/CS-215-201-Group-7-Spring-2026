import networkx as nx
import matplotlib.pyplot as plt

def draw_graph_with_yen_paths(G, yen_paths):
    pos = nx.spring_layout(G, seed=42)

    plt.figure(figsize=(10, 8))

    # Base graph
    nx.draw(
        G, pos,
        with_labels=True,
        node_size=500,
        node_color="lightblue",
        edge_color="lightgray"
    )

    # Edge weights
    edge_labels = nx.get_edge_attributes(G, "weight")
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels)

    colors = ["red", "green", "orange", "purple", "blue"]

    # Draw each Yen path
    for i, path in enumerate(yen_paths):
        edges = list(zip(path, path[1:]))
        nx.draw_networkx_edges(
            G,
            pos,
            edgelist=edges,
            edge_color=colors[i % len(colors)],
            width=3,
            label=f"Path {i+1}"
        )

    plt.legend()
    plt.title("Yen’s K-Shortest Paths on City Traffic Network")
    plt.show()
