#!/usr/bin/Rscript

args <- commandArgs(trailingOnly=TRUE)
if (length(args) < 1 ) { 
		cat("Please provide points for splitting\n")
		quit()
}
options(warn=-1)
library(GenSA)
pts  <- as.matrix(read.csv(args[1], header=TRUE, sep="\t"))
# number of dimensions of the Deployment Space
d <- ncol(pts)-1

score <- function(h) {
		temp <- as.data.frame(cbind(pts,res=apply(pts, 1, function(x) sum(x[1:d]*h[1:d])+h[d+1])))
		l1<- temp[temp$res < 0, 1:(d+1)]
		l2<- temp[temp$res >= 0, 1:(d+1)]
		if(nrow(l1) > d && nrow(l2)) {

				fit1 <- lm(y ~ ., l1)
				fit2 <- lm(y ~ ., l2)
				r1 <- summary(fit1)$r.squared
				r2 <- summary(fit2)$r.squared
				-((nrow(l1)*r1+nrow(l2)*r2))/(nrow(l1)+nrow(l2))
		} else {
				0
		}
}

lower=rep(-1, d+1)
upper=rep(1, d+1)
sa <- GenSA(fn=score, lower=lower, upper=upper, control=list(max.time=1))
cat(sa$par, sep=" ")
