import numpy as np
from matplotlib import pyplot as plt

def sigmoid(x):
	return 1/(1+np.exp(-x))

def dsigmoid(x):
	return sigmoid(x)*(1-sigmoid(x))


#   /**
#    * @description: sigmoid 曲线生成，当fStart<fStop时，曲线为上升曲线，反之为下降曲线，相等时为平行直线
#    * @param len:S曲线的长度，即采样点个数
#    * @param fStart：曲线的起始值
#    * @param fStop：曲线的结束值
#    * @param flexible：曲线的拉伸变换，越大代表压缩的最厉害，中间（x坐标0点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。
#    * @param index：曲线索引点，区间[0,len]
#    * @return fCurrent：索引点对应的曲线幅值
#    */
# len:int, fStart:float, fStop:float, flexible:float,
def sCurve(index) :
	len = 100000
	fStart = 0.01
	fStop = 1.0
	flexible = 6
	# if index > len:
	# 	index = len
	num = len/2
	melo = flexible * (index - num) / num
	deno = 1.0 / (1 + np.exp(-melo))
	fCurrent = fStart - (fStart - fStop) * deno
	return fCurrent
  

#   /**
#    * @description: 基于sigmoid曲线公式的联合曲线积分铸造
#    * @param cap: token理论上限值
#    * @param initialPrice：初始定价
#    * @param finalPrice：稳定定价
#    * @param flexible：曲线的拉伸变换，越大代表压缩的最厉害，中间（x坐标cap/2点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。
#    * @param currentSupply：当前代币供应量，区间[0,cap]
#    * @return currentPrice：当前mint价格
#    */
# cap:int, initialPrice:float, finalPrice:float, flexible:float,
def bondingCurve(currentSupply) :
	cap = 21000000		#2100万
	initialPrice = 0.01
	finalPrice = 1.0
	flexible = 6
	# if currentSupply > cap:
	# 	currentSupply = cap
	num = cap/2
	melo = flexible * (currentSupply - num) / num
	deno = 1.0 / (1 + np.exp(-melo))
	currentPrice = initialPrice - (initialPrice - finalPrice) * deno
	return currentPrice


x = np.linspace(-10,10,num=100)
y = sigmoid(x)
dy= dsigmoid(x)


currentSupply = np.linspace(0,21000000,num=21000000)
currentPrice = bondingCurve(currentSupply)

plt.subplot(1,3,1)
plt.plot(x,y)
plt.subplot(1,3,2)
plt.plot(x,dy)
plt.subplot(1,3,3)
plt.plot(currentSupply,currentPrice)
plt.show()
