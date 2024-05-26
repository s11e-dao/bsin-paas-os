import numpy as np
from matplotlib import pyplot as plt

def sigmoid(x):
	return 1/(1+np.exp(-x))

def dsigmoid(x):
	return sigmoid(x)*(1-sigmoid(x))

x = np.linspace(-10,10,num=100)
y = sigmoid(x)
dy= dsigmoid(x)

plt.subplot(1,2,1)
plt.plot(x,y)
plt.subplot(1,2,2)
plt.plot(x,dy)
plt.show()
