program obr
implicit none
real fi1(40), sigmafi1, psio(40), sigmapsio, psie(40), sigmapsie, A, sigmaA, NUM, psiomin, psiemin, fi1o, fi1e !входные данные
real n(40), costh(40), sigman(40), fi2o(40), fi2e(40) !данные из входных
real X2, k, mink, minX2, flag, sum1, sum2, nal, nb, na, PI, M(2,2), OM(2,2), b0, b1, detM, k0  !вспомогательные переменные
real no, sigmano, ne, sigmane !выходные данные
integer i, j, l
open(1,file="fi1.txt")
open(2,file="psio.txt")
open(3,file="psie.txt")
M = 0
OM = 0
fi1 = 0
sigmafi1 = 0
psio = 0
sigmapsio = 0
psie = 0
sigmapsie = 0
fi2o = 0
fi2e = 0
psiomin = 0
psiemin = 0
X2 = 0
k = 0
mink = 0
minX2 = 0
b0 = 0
b1 = 0
PI = 3.1415926535
100 format(f6.3)

!ввод данных
write(*,*) "enter 0 to check adjustment"
write(*,*) "enter 1 to calculate by general method"
write(*,*) "enter 2 to calculate by lesser difraction angle"
write(*,*) "enter 3 to calculate by angle of full inner reflection"
read(*,*) flag
write(*,*) "enter A"
read(*,*) A
if(flag == 0 .or. flag == 1) then
  write(*,*) "enter sA"
  read(*,*) sigmaA
  write(*,*) "enter NUM"
  read(*,*) NUM
  write(*,*) "enter fi1"
  do i = 1, NUM
    read(*,*) fi1(i)
    !read(1,*) fi1(i)
  end do
  write(*,*) "enter sfi1"
  read(*,*) sigmafi1
  write(*,*) "enter psio"
  do i = 1, NUM
    read(*,*) psio(i)
    !read(2,*) psio(i)
  end do
  write(*,*) "enter spsio"
  read(*,*) sigmapsio
end if
if(flag == 1) then
  write(*,*) "enter psie"
  do i = 1, NUM
    read(*,*) psie(i)
    !read(3,*) psie(i)
  end do
  write(*,*) "enter spsie"
  read(*,*) sigmapsie
end if
if(flag == 2)then
  write(*,*) "enter psiomin"
  read(*,*) psiomin
  write(*,*) "enter psiemin"
  read(*,*) psiemin
end if
if(flag == 3) then
  write(*,*) "enter fi1o"
  read(*,*) fi1o
  write(*,*) "enter fi1e"
  read(*,*) fi1e
end if

!перевод данных в нужные
fi2o = A + psio - fi1
fi2e = A + psie - fi1
A = A*PI/180
sigmaA = sigmaA*PI/180
fi1 = fi1*PI/180
sigmafi1 = sigmafi1*PI/180
fi2o = fi2o*PI/180
sigmapsio = sigmapsio*PI/180
fi2e = fi2e*PI/180
sigmapsie = sigmapsie*PI/180
psiomin = psiomin*PI/180
psiemin = psiemin*PI/180
fi1o = fi1o*PI/180
fi1e = fi1e*PI/180

!проверка юстировки
if(flag == 0) then
  do i = 1,NUM
    n(i) = sqrt((sin(fi1(i)))**2+(sin(fi2o(i)))**2+(2*sin(fi1(i))*sin(fi2o(i))*cos(A)))/sin(A)
    costh(i) = sin(fi1(i))/n(i)
    nal = (0.5*sin(2*fi1(i)) - 0.5*sin(2*fi2o(i)) - cos(A)*sin(fi1(i)-fi2o(i)))/(2*(n(i)*sin(A)**2))
	nb = -(sin(fi2o(i))*cos(fi2o(i)) + cos(A)*sin(fi1(i))*cos(fi2o(i)))/(n(i)*sin(A)**2)
	na = -(n(i)*cos(A))/sin(A) + (0.5*sin(2*fi2o(i)) + sin(fi1(i))*cos(A+fi2o(i)))/(n(i)*sin(A)**2)
    sigman(i) = sqrt((2*sigmafi1*nal)**2 + (nb*sigmapsio)**2 + (na*sigmaA)**2)
  end do
  k0 = 0
  do l = 1, 1000
    k = -1
    do j = 1, 1000
      X2 = 0
      do i = 1,NUM
        X2 = X2 + ((n(i) - k*(costh(i)**2)- k0)/sigman(i))**2
      end do
      if(minX2 == 0) then
        minX2 = X2
	    mink = k
      endif
      if(minX2 .ne. 0 .and. X2 < minX2) then
        minX2 = X2
	    mink = k
      end if
      k = k + 0.002
    end do
	k0 = k0 + 0.01
  end do
  !write(*,*) mink
  if(abs(mink) <= 0.02) then
    write(*,*) "correct adjustment"
  else
    write(*,*) "incorrect adjustment"
  end if
end if

!подсчет классическим методом
if(flag == 1) then

  !подсчет no
  do i = 1,NUM
    n(i) = sqrt((sin(fi1(i)))**2+(sin(fi2o(i)))**2+(2*sin(fi1(i))*sin(fi2o(i))*cos(A)))/sin(A)
    costh(i) = sin(fi1(i))/n(i)
    nal = (0.5*sin(2*fi1(i)) - 0.5*sin(2*fi2o(i)) - cos(A)*sin(fi1(i)-fi2o(i)))/(2*(n(i)*sin(A)**2))
    nb = -(sin(fi2o(i))*cos(fi2o(i)) + cos(A)*sin(fi1(i))*cos(fi2o(i)))/(n(i)*sin(A)**2)
    na = -(n(i)*cos(A))/sin(A) + (0.5*sin(2*fi2o(i)) + sin(fi1(i))*cos(A+fi2o(i)))/(n(i)*sin(A)**2)
    sigman(i) = sqrt((2*sigmafi1*nal)**2 + (nb*sigmapsio)**2 + (na*sigmaA)**2)
  end do
  sum1 = 0
  sum2 = 0
  do i = 1,NUM
     sum1 = sum1 + n(i)/(sigman(i)**2)
     sum2 = sum2 + 1/(sigman(i)**2)
  end do
  no = sum1/sum2
  sigmano = sqrt(1/sum2)
  write(*,100) no, sigmano

  !подсчет ne
  do i = 1, NUM
    n(i) = sqrt((sin(fi1(i)))**2+(sin(fi2e(i)))**2+(2*sin(fi1(i))*sin(fi2e(i))*cos(A)))/sin(A)
    nal = (0.5*sin(2*fi1(i)) - 0.5*sin(2*fi2e(i)) - cos(A)*sin(fi1(i)-fi2e(i)))/(2*(n(i)*sin(A)**2))
    nb = -(sin(fi2e(i))*cos(fi2e(i)) + cos(A)*sin(fi1(i))*cos(fi2e(i)))/(n(i)*sin(A)**2)
    na = -(n(i)*cos(A))/sin(A) + (0.5*sin(2*fi2e(i)) + sin(fi1(i))*cos(A+fi2e(i)))/(n(i)*sin(A)**2)
    sigman(i) = sqrt((2*sigmafi1*nal)**2 + (nb*sigmapsie)**2 + (na*sigmaA)**2)
    M(1,1) = M(1,1) + 1/(2*sigman(i)/(n(i)**3))**2
    M(1,2) = M(1,2) + (sin(fi1(i))/n(i))**2/(2*sigman(i)/(n(i)**3))**2
    M(2,1) = M(2,1) + (sin(fi1(i))/n(i))**2/(2*sigman(i)/(n(i)**3))**2
    M(2,2) = M(2,2) + (sin(fi1(i))/n(i))**4/(2*sigman(i)/(n(i)**3))**2
    b0 = b0 + (n(i)**(-2))/(2*sigman(i)/(n(i)**3))**2
    b1 = b1 + (n(i)**(-2))*(sin(fi1(i))/n(i))**2/(2*sigman(i)/(n(i)**3))**2
  end do
  detM = M(1,1)*M(2,2)-M(2,1)*M(1,2)
  OM(1,1) = M(2,2)/detM
  OM(1,2) = -M(1,2)/detM
  OM(2,1) = -M(2,1)/detM
  OM(2,2) = M(1,1)/detM
  ne = 1/sqrt(OM(1,1)*b0+OM(1,2)*b1)
  no = 1/sqrt(OM(2,1)*b0+OM(2,2)*b1+ne**(-2))
  sigmane = sqrt(OM(1,1))*0.5*(OM(1,1)*b0+OM(1,2)*b1)**(-3/2)
  write(*,100) ne, sigmane
  end if

!подсчет no и ne через угол наимеьшего отклонения
if(flag == 2) then
  no = sin((psiomin+A)/2)/sin(A/2)
  ne = sin((psiemin+A)/2)/sin(A/2)
  write(*,100) no, ne
end if

!подсчет no и ne через угол полного отражения
if(flag == 3) then
  no = sqrt((sin(fi1o))**2+1+(2*sin(fi1o)*cos(A)))/sin(A)
  ne = sqrt((sin(fi1e))**2+1+(2*sin(fi1e)*cos(A)))/sin(A)
  write(*,100) no, ne
end if

close(1)
close(2)
close(3)
end