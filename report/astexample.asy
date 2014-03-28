import drawtree;

TreeNode root = makeNode("*");

TreeNode n11 = makeNode(root, "+");
TreeNode n12 = makeNode(root, "2");

TreeNode n21 = makeNode(n11, "x");
TreeNode n22 = makeNode(n11, "1");

draw(root, (0,0));
